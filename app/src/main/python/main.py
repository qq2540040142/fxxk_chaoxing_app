import foreWork
import doWork
from java import jclass
isRunning = True
fragment = jclass("com.ysl.chaoxing.fragment.StartFragment")
instance = None
def set_running(isrun):
    global isRunning
    isRunning = isrun

def main(usernm,userId,session,num):
    """
    :param usernm: 用户名
    :param userId: 用户ID
    :param session:
    :param num: 课程下标
    :return:
    """
    instance = fragment.getInstance()
    # 从本地或在线获取课程信息以及各类任务点信息
    chapterids, course, isLocal = foreWork.find_allNode(num, session)
    if isRunning is False :
        instance.addText("python已安全退出!")
        set_running(True)
        return
    #在用户选择的课程中获取所有的任务点
    jobs = foreWork.find_objectives(chapterids, course['courseid'], session)
    if isRunning is False :
        instance.addText("python已安全退出!")
        set_running(True)
        return
    #判断任务点类型
    mp4, ppt = foreWork.detect_job_type(jobs, usernm, course['courseid'])
    if isRunning is False :
        instance.addText("python已安全退出!")
        set_running(True)
        return
    #获取超星的openc加密密文
    course = foreWork.get_openc(usernm, course, session)
    # 开始完成MP4任务点
    doWork.do_mp4(usernm,userId, course, session, mp4)


