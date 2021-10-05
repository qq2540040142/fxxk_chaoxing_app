import json
import re
import requests
from os.path import exists
from java import jclass
import requests.utils
from rich.console import Console
from rich.table import Table
import time
import main


console = Console()

channelList = None
fragment = jclass("com.ysl.chaoxing.fragment.StartFragment")
instance = None

def find_courses(usernm, session):
    """
    获取用户的所有课程列表
    :param usernm: 用户名
    :param session: requests.session()对象
    :return:
    """
    console.log("开始获取用户的所有[yellow]课程信息[/yellow]")

    table = Table(show_header=True, header_style="bold magenta")
    table.add_column("序号", style="dim")
    table.add_column("课程名")
    table.add_column("记录")

    header = {'Accept-Encoding': 'gzip',
              'Accept-Language': 'zh_CN',
              'Host': 'mooc1-api.chaoxing.com',
              'Connection': 'Keep-Alive',
              'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G9350 Build/LMY48Z) '
                            'com.chaoxing.mobile/ChaoXingStudy_3_5.21_android_phone_206_1 (SM-G9350; Android 5.1.1; '
                            'zh_CN)_1969814533 '
              }
    my_course = session.get("http://mooc1-api.chaoxing.com/mycourse?rss=1&mcode=", headers=header)
    result = my_course.json()
    # console.log(result)
    global channelList
    channelList = result['channelList']
    javaBean = jclass("com.ysl.chaoxing.data.AllSubject")
    subjectData = javaBean()
    for item in channelList:
        try:
            # 往java类中添加数据
            subjectData.addName(item['content']['course']['data'][0]['name'])
            subjectData.addCpi(item['cpi'])
            subjectData.addContentId(item['content']['id'])
            subjectData.addCourseId(item['content']['course']['data'][0]['id'])
            subjectData.addTeacherName(item['content']['course']['data'][0]['teacherfactor'])
            subjectData.addSchoolName("null")
        except Exception:
            pass
    return subjectData


def find_allNode(num,session):
    """
    遍历当前课程所有节点
    :param num:  选择的课程下标
    :param session:
    :return:
    """
    chapterids = []
    course = {}
    header = {'Accept-Encoding': 'gzip',
              'Accept-Language': 'zh_CN',
              'Host': 'mooc1-api.chaoxing.com',
              'Connection': 'Keep-Alive',
              'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G9350 Build/LMY48Z) '
                            'com.chaoxing.mobile/ChaoXingStudy_3_5.21_android_phone_206_1 (SM-G9350; Android 5.1.1; '
                            'zh_CN)_1969814533 '
              }
    channelList_json = channelList[num]
    course['cpi'] = channelList_json['cpi']
    course['clazzid'] = channelList_json['content']['id']
    course['courseid'] = channelList_json['content']['course']['data'][0]['id']
    global instance
    instance = fragment.getInstance()
    instance.addText( "课程名称:" + channelList[num]['content']['course']['data'][0]['name'])
    instance.addText("讲师：" + channelList[num]['content']['course']['data'][0]['teacherfactor'])
    instance.addText("正在尝试在线读取课程记录")
    url = 'https://mooc1-1.chaoxing.com/visit/stucoursemiddle?courseid=' + str(
        course['courseid']) + '&clazzid=' + str(course['clazzid']) + '&vc=1&cpi=' + str(
        course['cpi'])
    resp = session.get(url, headers=header)
    content = resp.text
    for chapter in re.findall('\?chapterId=(.*?)&', content):
        chapterids.append(str(chapter))
    course['enc'] = re.findall("&clazzid=.*?&enc=(.*?)'", content)[0]
    instance.addText("在线课程记录读取成功")
    return chapterids, course, False


def find_objectives(chapterids, course_id, session):
    """
    在用户选择的课程中获取所有的任务点
    :param usernm: 用户名
    :param chapterids: 章节编号
    :param course_id: 课程编号
    :param session: requests.session()
    :return:
    """
    jobs = {}
    for lesson_id in chapterids:
        if main.isRunning is False:
            break
        url = 'http://mooc1-api.chaoxing.com/gas/knowledge?id=' + str(
            lesson_id) + '&courseid=' + str(
            course_id) + '&fields=begintime,clickcount,createtime,description,indexorder,jobUnfinishedCount,jobcount,' \
                         'jobfinishcount,label,lastmodifytime,layer,listPosition,name,openlock,parentnodeid,status,' \
                         'id,card.fields(cardIndex,cardorder,description,knowledgeTitile,knowledgeid,theme,title,' \
                         'id).contentcard(all)&view=json '
        header = {
            'Accept-Language': 'zh_CN',
            'Host': 'mooc1-api.chaoxing.com',
            'Connection': 'Keep-Alive',
            'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G9350 Build/LMY48Z) '
                          'com.chaoxing.mobile/ChaoXingStudy_3_5.21_android_phone_206_1 (SM-G9350; Android 5.1.1; '
                          'zh_CN)_19698145335.21 '
        }
        resp = session.get(url, headers=header)
        try:
            if resp.content:
                content = str(json.loads(resp.text)['data'][0]['card']['data']).replace('&quot;',
                                                                                        '')
                result = re.findall('{objectid:(.*?),.*?,_jobid:(.*?),', content)
                jobs[lesson_id] = result

                instance.addText(
                    '在章节{}中找到{}个任务点'.format(lesson_id, len(result)))
        except Exception as e:
            instance.addText('错误类型:{}'.format(e.__class__.__name__))
            instance.addText('错误明细:{}'.format(e))
        time.sleep(0.3)
    if main.isRunning is False :
        return
    return jobs


def detect_job_type(jobs, usernm, course_id):
    """
    识别任务点的类型(mp4/ppt)
    :param jobs: 任务点信息
    :param usernm: 用户名
    :param course_id: 课程编号
    :return:
    """
    instance.addText("正在尝试识别任务点类型")
    mp4 = {}
    ppt = {}
    for chapter in jobs:
        if main.isRunning is False:
            break
        for item in jobs[chapter]:
            if main.isRunning is False:
                break
            url = 'https://mooc1-api.chaoxing.com/ananas/status/' + item[0]
            header = {
                'Host': 'mooc1-api.chaoxing.com',
                'Connection': 'keep-alive',
                'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 5.1.1; SM-G9350 Build/LMY48Z) '
                              'com.chaoxing.mobile/ChaoXingStudy_3_5.21_android_phone_206_1 (SM-G9350; Android 5.1.1; '
                              'zh_CN)_19698145335.21',
                'Accept': '*/*',
                'Accept-Encoding': 'gzip, deflate',
                'Accept-Language': 'zh-CN,en-US;q=0.8',
            }
            resp = requests.get(url, headers=header)
            result = resp.json()
            rtn = job_type(chapter, item, result)
            if rtn['type'] == 'mp4':
                mp4[item[0]] = rtn['detail']
            elif rtn['type'] == 'ppt':
                ppt[item[0]] = rtn['detail']
            else:
                pass
    if main.isRunning is False:
        return
    instance.addText('共加载任务点{}个'.format(len(mp4) + len(ppt)))
    return mp4, ppt


def job_type(chapter, item, content):
    """
    获取任务点信息
    :param chapter: 章节编号
    :param item: 任务点名
    :param content: 任务点信息
    :return:
    """
    try:
        filename = content['filename']
        if 'mp4' in filename or 'flv' in filename:
            object_mp4 = []
            object_mp4.append(content['filename'])
            object_mp4.append(content['dtoken'])
            object_mp4.append(content['duration'])
            object_mp4.append(content['crc'])
            object_mp4.append(content['key'])
            object_mp4.append(item)
            object_mp4.append(chapter)
            # mp4[item[0]] = object_mp4
            instance.addText('添加mp4任务{}成功'.format(content['filename']))
            return {'type': 'mp4', 'detail': object_mp4}
        elif 'ppt' in filename:
            object_ppt = []
            object_ppt.append(content['crc'])
            object_ppt.append(content['key'])
            object_ppt.append(content['filename'])
            object_ppt.append(content['pagenum'])
            object_ppt.append(item)
            object_ppt.append(chapter)
            # ppt[item[0]] = object_ppt
            instance.addText('添加ppt任务{}成功'.format(content['filename']))
            return {'type': 'ppt', 'detail': object_ppt}
        else:
            instance.addText('未检测出任务类型')
            return {'type': 'none'}
    except Exception as e:
        instance.addText('任务点识别失败:{}'.format(e))
        return {'type': 'none'}


def get_openc(usernm, course, session):
    """
    获取超星的openc加密密文
    :param usernm: 用户名
    :param course: 课程信息
    :param session: requests.session()
    :return:
    """
    url = 'https://mooc1-1.chaoxing.com/visit/stucoursemiddle?courseid={}&clazzid={}&vc=1&cpi={}'.format(
        course['courseid'], course['clazzid'], course['cpi'])
    header = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                      'Chrome/86.0.4240.198 Safari/537.36',
    }
    resp = session.get(url, headers=header)
    try:
        course['openc'] = re.findall("openc : '(.*?)'", resp.text)[0]
    except Exception:
        course['openc'] = re.findall('&openc=(.*?)"', resp.text)[0]
    instance.addText('成功获取openc参数:{}'.format(course['openc']))
    return course


def get_forework_done(usernm, session,num):
    """
    整合上述所有函数的内容
    :param usernm: 用户名
    :param session: requests.session()
    :return:
    """
    chapterids, course, isLocal = find_allNode(num, session)
    instance.addText("开始在线获取课程所有信息")
    jobs = find_objectives(chapterids, course['courseid'], session)
    mp4, ppt = detect_job_type(jobs, usernm, course['courseid'])
    course = get_openc(usernm, course, session)
    return jobs, course, mp4, ppt

