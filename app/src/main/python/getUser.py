import hashlib
import json
import requests
import time
from os import listdir

import requests.utils
from rich.console import Console
from rich.table import Table

console = Console()
userId = None
def get_encoded_token():
    """
    通过逆向得到的超星学习通登录加密算法，根据当前时间戳计算得出加密结果并返回
    :return: 当前时间戳(str),加密结果(str)
    """
    tsp = int(time.time() * 1000)
    m_token = '4faa8662c59590c6f43ae9fe5b002b42'
    m_encrypt_str = 'token=' + m_token + '&_time=' + str(tsp) + '&DESKey=Z(AfY@XS'
    md5 = hashlib.md5()
    md5.update(m_encrypt_str.encode('utf-8'))
    enc = md5.hexdigest()
    return str(tsp), enc


def get_data(usernm, passwd):
    """
    获取发送登录POST请求时需要用到的data内容
    :param usernm: 用户名
    :param passwd: 密码
    :return: 发送登录POST请求的data参数
    """
    data = ''
    data += '--vfV33Hae5dKmSaPrHidgXv4ZK-3gOyNn-jid8-6\r\nContent-Disposition: form-data; name="uname"\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit\r\n\r\n'
    data += usernm + '\r\n'
    data += '--vfV33Hae5dKmSaPrHidgXv4ZK-3gOyNn-jid8-6\r\nContent-Disposition: form-data; name="code"\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit\r\n\r\n'
    data += passwd + '\r\n'
    data += '--vfV33Hae5dKmSaPrHidgXv4ZK-3gOyNn-jid8-6\r\nContent-Disposition: form-data; name="loginType"\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit\r\n\r\n'
    data += '1\r\n'
    data += '--vfV33Hae5dKmSaPrHidgXv4ZK-3gOyNn-jid8-6\r\nContent-Disposition: form-data; name="roleSelect"\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit\r\n\r\n'
    data += 'true\r\n--vfV33Hae5dKmSaPrHidgXv4ZK-3gOyNn-jid8-6--\r\n'
    return data

def login(usernm, passwd):
    """
    尝试登录，获取登录用户的学校fid号码以及学生的uid号码，保存至本地
    :param usernm: 用户名
    :param passwd: 密码
    :return: requests.session()对象
    """
    user = {}
    header = {'Accept-Language': 'zh_CN',
              'Content-Type': 'multipart/form-data; boundary=vfV33Hae5dKmSaPrHidgXv4ZK-3gOyNn-jid8-6',
              'Host': 'passport2.chaoxing.com',
              'Connection': 'Keep-Alive',
              'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G9350 Build/LMY48Z) com.chaoxing.mobile/ChaoXingStudy_3_5.21_android_phone_206_1 (SM-G9350; Android 5.1.1; zh_CN)_1969814533'
              }
    console.log("正在开始尝试[yellow]登录账号[/yellow]")
    session = requests.session()
    tsp, enc = get_encoded_token()
    post_url = 'http://passport2.chaoxing.com/xxt/loginregisternew?' + 'token=4faa8662c59590c6f43ae9fe5b002b42' + '&_time=' + tsp + '&inf_enc=' + enc
    resp = session.post(post_url, data=get_data(usernm, passwd), headers=header)
    result = resp.json()
    if result['status']:
        cookie = requests.utils.dict_from_cookiejar(resp.cookies)
        global userId
        userId = cookie['_uid']
    else:
        console.log("[red]登录失败[/red],请检查你的[red]账号密码[/red]是否正确,按回车键退出")
        return None
    return session

def getUserId():
    return userId