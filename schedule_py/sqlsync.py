import pymysql
import pandas as pd
from sshtunnel import SSHTunnelForwarder

source_table = 'sada_gdpi_click_dtl'
target_table = source_table+'_bk'

# 连接数据库
conn1 = pymysql.connect(host='172.28.72.225', user='root', port=7487,
                    password='l84kCG5KP4uNNtRX', database='lynntest')

server = SSHTunnelForwarder(
    ('172.28.72.225', 22), # 指定 ssh 登录的跳转机的 address，端口号
    ssh_username='root', # 远程服务器的用户名，注意不是 DB 的用户名和密码
    ssh_password='GDS&123softswitch', # 远程服务器的密码
    remote_bind_address=('172.28.160.99', 13306), # 注意端口号不要加引号
    # local_bind_address=('127.0.0.1', 3306)#端口可以自己设置，没有冲突即可，不加这个参数也可
)
server.start()

# 数据库表是com_tran_info userlog 的sada_gdpi_click
conn2 = pymysql.connect(host='127.0.0.1',
                port=server.local_bind_port, # server.local_bind_port, 
                user='duser',
                password='RAkj#7853',
                database='userlog',
                connect_timeout=10,
            )

 

def get_all_data(cursor, table_name):
    cursor.execute("select * from " + table_name)
    results = cursor.fetchall()
    description = cursor.description
    # table head
    df = pd.DataFrame(data=results, columns=[item[0] for item in description])
    return df
 
df = get_all_data(conn2.cursor(), table_name=source_table)

cur1 = conn1.cursor()
cur2 = conn2.cursor()

# 删除表
create_query = 'DROP TABLE IF EXISTS %s'% (target_table)
cur1.execute(create_query)

# 执行查询语句
cur2.execute(f"SHOW COLUMNS FROM {source_table}")
# 获取查询结果
results = cur2.fetchall()
# 创建表
create_query = 'CREATE TABLE IF NOT EXISTS %s ('%target_table
for k in results:
    create_query += '%s %s, '%(k[0],k[1])
create_query = create_query[:-2]
create_query += ')'

cur1.execute(create_query)

# 显示所有表名
cur1.execute('SHOW TABLES')
tables = cur1.fetchall()

# 插入数据
insert_data = df.values.tolist()
insert_query = 'INSERT INTO %s ('%target_table
for k in df.keys():
    insert_query += '%s, '%k
insert_query = insert_query[:-2] + ') VALUES ('
for i in range(len(df.keys())):
    insert_query += '%s, '
insert_query = insert_query[:-2]+')'

cur1.executemany(insert_query, insert_data)
conn1.commit()

cur1.close()
cur2.close()
conn1.close()
conn2.close()