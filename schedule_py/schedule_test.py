import pymysql
import pandas as pd
import random
import numpy as np
import json
import socket
import schedule
import time
import os
from pathlib import Path

json_file_path = 'table_config.json'

# 读取JSON文件
with open(json_file_path, 'r', encoding='utf-8') as file:
    datas = json.load(file)

split_info = datas['table-config']['tables'][1]['split_tables']

source_table = datas['table-config']['tables'][0]['real_table_name']

# 连接数据库
conn1 = pymysql.connect(host='172.28.72.225', user='root', port=7487,
                        password='l84kCG5KP4uNNtRX', database='dgut')
if 1:
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

cur1 = conn1.cursor()
cur2 = conn2.cursor()


def query_database():
    query_ins = "SELECT * FROM sada_gdpi_click_dtl_dgut_ins"
    cur2.execute(query_ins)
    ins_results = cur2.fetchall()

    # 查询删除数据的SQL
    query_del = "SELECT * FROM sada_gdpi_click_dtl_dgut_del"
    cur2.execute(query_del)
    del_results = cur2.fetchall()

    # 查询更新数据的SQL
    query_upd = "SELECT * FROM sada_gdpi_click_dtl_dgut_upd"
    cur2.execute(query_upd)
    upd_results = cur2.fetchall()

    # 处理新增、删除、更新数据
    if ins_results:
        handle_insert(ins_results)
    if del_results:
        handle_delete(del_results)
    if upd_results:
        handle_update(upd_results)


# 处理插入
def handle_insert(results):

    description = cur2.description
    results = np.array(results)
    key_dict = {}
    for k, kl in enumerate(description):
        key_dict[kl[0]] = k
    for i in range(len(split_info)):
        table_name = source_table + '_' + split_info[i]['split_table_name']
        columns_info = split_info[i]['columns']
        drop_query = 'DROP TABLE IF EXISTS %s' % table_name
        cur1.execute(drop_query)
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
        for k in columns_info:
            create_query += '%s %s(%s), ' % (k['name'], k['type'], k['length'])
        create_query = create_query[:-2]
        create_query += ')'
        create_query
        print(create_query)
        cur1.execute(create_query)
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

    # 插入子表
    insert_query_dict = {}
    for table_name in target_columns:
        insert_query = 'INSERT INTO %s (' % table_name
        columns_list = target_columns[table_name]
        for k in columns_list:
            insert_query += '%s, ' % k
        insert_query = insert_query[:-2] + ') VALUES ('
        for i in range(len(columns_list)):
            insert_query += '%s, '
        insert_query = insert_query[:-2] + ')'
        insert_query_dict[table_name] = insert_query

    i = 0
    for table_name in target_columns:
        if table_name.find('relation') != -1:
            source_keys = [key_dict['sid']]
        else:
            source_keys = [key_dict[f'sid{i}']]
            i = i + 1
        for k in target_columns[table_name]:
            if k != 'sid':
                source_keys.append(key_dict[k])
        insert_data = results[:, source_keys].tolist()
        insert_query = insert_query_dict[table_name]
        cur1.executemany(insert_query, insert_data)
        conn1.commit()

#         清空 sada_gdpi_click_dtl_dgut_ins


# 处理删除数据
def handle_delete(results):
    print("处理删除操作", results)


# 处理更新数据
def handle_update(results):
    print("处理更新操作", results)


query_database()
