import pymysql
import random
import numpy as np
import json
import socket
import time
import os
from pathlib import Path

json_file_path = 'table_config.json'

# 读取JSON文件
with open(json_file_path, 'r', encoding='utf-8') as file:
    datas = json.load(file)

split_info = datas['table-config']['tables'][1]['split_tables']
source_table = datas['table-config']['tables'][0]['real_table_name']

# 连接数据库函数
def connect_db():
    print("开始连接")
    conn1 = pymysql.connect(host='172.28.72.225', user='root', port=7487,
                            password='l84kCG5KP4uNNtRX', database='dgut')
    conn2 = None
    if 0:
        from sshtunnel import SSHTunnelForwarder

        server = SSHTunnelForwarder(
            ('172.28.72.225', 22),  # 指定 ssh 登录的跳转机的 address，端口号
            ssh_username='root',  # 远程服务器的用户名，注意不是 DB 的用户名和密码
            ssh_password='GDS&123softswitch',  # 远程服务器的密码
            remote_bind_address=('172.28.160.99', 13306),  # 注意端口号不要加引号
            # local_bind_address=('127.0.0.1', 3306)#端口可以自己设置，没有冲突即可，不加这个参数也可
        )
        server.start()

        # 数据库表是com_tran_info userlog 的sada_gdpi_click
        conn2 = pymysql.connect(host='127.0.0.1',
                                port=server.local_bind_port,  # server.local_bind_port,
                                user='duser',
                                password='RAkj#7853',
                                database='userlog1',
                                connect_timeout=10,
                                )
    else:
        conn2 = pymysql.connect(host='172.28.160.99',
                                port=13306,  # server.local_bind_port,
                                user='duser',
                                password='RAkj#7853',
                                database='userlog1',
                                connect_timeout=10,
                                )
    print("连接成功")

    return conn1, conn2


def query_database():
    conn1, conn2 = connect_db()
    cur1 = conn1.cursor()
    cur2 = conn2.cursor()

    query_ins = "SELECT * FROM sada_gdpi_click_dtl_dgut_ins"
    cur2.execute(query_ins)
    ins_results = cur2.fetchall()
    print("检测更新数据")

    if ins_results:
        print("检测到新增数据")
        handle_insert(ins_results, cur1, conn1, cur2)
        query_clear = "TRUNCATE table sada_gdpi_click_dtl_dgut_ins"
        cur2.execute(query_clear)
        cur2.fetchall()
    else:
        print("没有检测到新增数据")

    query_del = "SELECT * FROM sada_gdpi_click_dtl_dgut_del"
    cur2.execute(query_del)
    del_results = cur2.fetchall()
    print("检测删除数据")
    if del_results:
        print("检测到删除数据")
        handle_delete(del_results, cur1, conn1, cur2)
        query_clear = "TRUNCATE table sada_gdpi_click_dtl_dgut_del"
        cur2.execute(query_clear)
        cur2.connection.commit()
    else:
        print("没有检测到删除数据")

    query_upd = "SELECT * FROM sada_gdpi_click_dtl_dgut_upd"
    cur2.execute(query_upd)
    upd_results = cur2.fetchall()
    print("检测更新数据")
    if upd_results:
        print("检测到修改数据")
        handle_update(upd_results, cur1, conn1, cur2)
        query_clear = "TRUNCATE table sada_gdpi_click_dtl_dgut_upd"
        cur2.execute(query_clear)
        cur2.connection.commit()
    else:
        print("没有检测到修改数据")

    # 关闭连接
    print("处理结束，关闭连接")
    cur1.close()
    conn1.close()
    cur2.close()
    conn2.close()


# 处理插入
def handle_insert(results, cur1, conn1, cur2):
    print("处理插入操作", results)
    description = cur2.description
    results = np.array(results)
    key_dict = {kl[0]: k for k, kl in enumerate(description)}

    for i in range(len(split_info)):
        table_name = source_table + '_' + split_info[i]['split_table_name']
        columns_info = split_info[i]['columns']
        drop_query = 'DROP TABLE IF EXISTS %s' % table_name
        # cur1.execute(drop_query)
        if split_info[i]['split_table_name'] != 'relation':
            create_query = 'CREATE TABLE IF NOT EXISTS %s (sid INT, ' % table_name
        else:
            create_query = 'CREATE TABLE IF NOT EXISTS %s (' % table_name
            num_rows = results.shape[0]
            num_columns = len(columns_info) - 1
            insert_data = []

            for col in range(num_columns):
                unique_numbers = set()
                while len(unique_numbers) < num_rows:
                    random_num = random.randint(100000000, 999999999)
                    unique_numbers.add(random_num)
                unique_numbers = np.array(list(unique_numbers)).reshape(-1, 1)
                results = np.hstack([results, unique_numbers])
                key_dict[f'sid{col}'] = len(key_dict.keys())
        for k in columns_info:
            create_query += '%s %s(%s), ' % (k['name'], k['type'], k['length'])
        create_query = create_query[:-2] + ')'
        # cur1.execute(create_query)

    source_columns = list(key_dict.keys())
    target_columns = {}
    for i in range(len(split_info)):
        table_name = source_table + '_' + split_info[i]['split_table_name']
        columns_info = split_info[i]['columns']
        cross_columns = ['sid'] if split_info[i]['split_table_name'] != 'relation' else []
        for k in columns_info:
            cross_columns.append(k['name'])
        target_columns[table_name] = cross_columns

    insert_query_dict = {}
    for table_name, columns_list in target_columns.items():
        insert_query = 'INSERT INTO %s (' % table_name
        insert_query += ', '.join(columns_list) + ') VALUES (' + ', '.join(['%s'] * len(columns_list)) + ')'
        insert_query_dict[table_name] = insert_query

    i = 0
    for table_name in target_columns:
        source_keys = [key_dict['sid']] if 'relation' in table_name else [key_dict[f'sid{i}']]
        i += 1 if 'relation' not in table_name else 0
        source_keys += [key_dict[k] for k in target_columns[table_name] if k != 'sid']
        insert_data = results[:, source_keys].tolist()
        cur1.executemany(insert_query_dict[table_name], insert_data)
        conn1.commit()


# 处理删除数据
def handle_delete(results, cur1, conn1, cur2):
    print("处理删除操作", results)
    results = np.array(results)
    key_dict = {}
    for k, kl in enumerate(cur2.description):
        key_dict[kl[0]] = k
    for i in range(len(split_info)):
        table_name = source_table + '_' + split_info[i]['split_table_name']
        columns_info = split_info[i]['columns']
        # drop_query = 'DROP TABLE IF EXISTS %s' % table_name
        # cur1.execute(drop_query)
        if split_info[i]['split_table_name'] != 'relation':
            create_query = 'CREATE TABLE IF NOT EXISTS %s (sid INT, ' % table_name
        else:
            create_query = 'CREATE TABLE IF NOT EXISTS %s (' % table_name
            # 生成随机矩阵
            num_rows = results.shape[0]
            num_columns = len(columns_info) - 1  # 减去 'sid' 列

            # 创建插入数据的矩阵
            insert_data = []

            for col in range(num_columns):
                unique_numbers = set()
                while len(unique_numbers) < num_rows:
                    random_num = random.randint(100000000, 999999999)
                    unique_numbers.add(random_num)
                # 将生成的随机数添加到 DataFrame 中
                # df[f'sid{col}'] = list(unique_numbers)
                unique_numbers = np.array(list(unique_numbers))
                unique_numbers = unique_numbers.reshape(-1, 1)
                results = np.hstack([results, unique_numbers])
                key_dict[f'sid{col}'] = len(key_dict.keys())
    source_columns = list(key_dict.keys())
    target_columns = {}
    for i in range(len(split_info)):
        table_name = source_table + '_' + split_info[i]['split_table_name']
        columns_info = split_info[i]['columns']
        if split_info[i]['split_table_name'] != 'relation':
            cross_columns = ['sid']
        else:
            cross_columns = []
        for k in columns_info:
            match_name = k['name']
            # if match_name in source_columns:
            cross_columns.append(k['name'])
        target_columns[table_name] = cross_columns

        # 删除数据
    del_querys = []
    del_idxs = results[:, key_dict['sid']]

    del_key = 'sid'

    for table_name in target_columns:
        for idx in del_idxs:
            del_query = 'UPDATE table_name %s SET %s=0 WHERE %s=%s' % (table_name, del_key, del_key, idx)

            del_querys.append(del_query)
    for del_query in del_querys:
        try:
            print(del_query)
            cur1.execute(del_query)
        except:
            print("SQL Failed: %s" % del_query)
    conn1.commit()


# 处理更新数据
def handle_update(results, cur1, conn1, cur2):
    print("处理更新操作", results)
    handle_delete(results, cur1, conn1, cur2)
    handle_insert(results, cur1, conn1, cur2)

# 循环查询数据库
while True:
    query_database()
    print("等待15秒...")
    time.sleep(15)

