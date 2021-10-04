import foreWork
import doWork
def main(usernm,userId,session,num):
    """
    :param usernm: 用户名
    :param userId: 用户ID
    :param session:
    :param num: 课程下标
    :return:
    """
    # 从本地或在线获取课程信息以及各类任务点信息
    jobs, course, mp4, ppt = foreWork.get_forework_done(usernm, session,num)
    # 开始完成MP4任务点
    doWork.do_mp4(usernm,userId, course, session, mp4)
