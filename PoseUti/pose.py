import base64
from email.quoprimime import body_check
import site
from sqlite3 import Time
import string
import mediapipe as mp
import cv2
import numpy as np

mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles
mp_pose = mp.solutions.pose
""" 
    传图像进行姿势识别
"""
def Pose_recognition(img):
    image=img
    height,width,_ = image.shape
    mp_pose = mp.solutions.pose
    hoslitic= mp_pose.Pose(
        static_image_mode=True)
    results = hoslitic.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))

    return results
""" 
    检测胳膊和腿的角度
"""
def Arm_legPose(results,img):
    height,width,_ = img.shape
    Lshoulder=[results.pose_landmarks.landmark[11].x*width,results.pose_landmarks.landmark[11].y*height]
    Rshoulder=[results.pose_landmarks.landmark[12].x*width,results.pose_landmarks.landmark[12].y*height]
    Lelbow=[results.pose_landmarks.landmark[13].x*width,results.pose_landmarks.landmark[13].y*height]
    Relbow=[results.pose_landmarks.landmark[14].x*width,results.pose_landmarks.landmark[14].y*height]
    Lwrist=[results.pose_landmarks.landmark[15].x*width,results.pose_landmarks.landmark[15].y*height]
    Rwrist=[results.pose_landmarks.landmark[16].x*width,results.pose_landmarks.landmark[16].y*height]
    Lhip=[results.pose_landmarks.landmark[23].x*width,results.pose_landmarks.landmark[23].y*height]
    Rhip=[results.pose_landmarks.landmark[24].x*width,results.pose_landmarks.landmark[24].y*height]
    Lknee=[results.pose_landmarks.landmark[25].x*width,results.pose_landmarks.landmark[25].y*height]
    Rknee=[results.pose_landmarks.landmark[26].x*width,results.pose_landmarks.landmark[26].y*height]
    Lankle=[results.pose_landmarks.landmark[27].x*width,results.pose_landmarks.landmark[27].y*height]
    Rankle=[results.pose_landmarks.landmark[28].x*width,results.pose_landmarks.landmark[28].y*height]       
    """ 
    计算左右胳膊和左右腿的角度
    """ 
    angle_Larm= calculate_angle_2d(Lshoulder,Lelbow,Lwrist) 
    angle_Rarm= calculate_angle_2d(Rshoulder,Relbow,Rwrist)
    angle_Lleg= calculate_angle_2d(Lhip,Lknee,Lankle)
    angle_Rleg= calculate_angle_2d(Rhip,Rknee,Rankle)
    array = np.array([angle_Rarm,angle_Larm,angle_Rleg,angle_Lleg])
    return array       
""" 
 判断是站姿还是坐姿
""" 
def Sit_or_stand(results,img):
    body_pose="站姿"
    height,width,_ = img.shape
    Lshoulder=[results.pose_landmarks.landmark[11].x*width,results.pose_landmarks.landmark[11].y*height]
    Rshoulder=[results.pose_landmarks.landmark[12].x*width,results.pose_landmarks.landmark[12].y*height]
    Lhip=[results.pose_landmarks.landmark[23].x*width,results.pose_landmarks.landmark[23].y*height]
    Rhip=[results.pose_landmarks.landmark[24].x*width,results.pose_landmarks.landmark[24].y*height]
    Lknee=[results.pose_landmarks.landmark[25].x*width,results.pose_landmarks.landmark[25].y*height]
    Rknee=[results.pose_landmarks.landmark[26].x*width,results.pose_landmarks.landmark[26].y*height]
    """ 
    计算肩部，臀部，膝盖三个关节点的角度
    """ 
    angle_Lbody= calculate_angle_2d(Lshoulder,Lhip,Lknee) 
    angle_Rbody= calculate_angle_2d(Rshoulder,Rhip,Rknee)
    """ 
     当三个关节点角度小于150.更改body_pose为坐姿
     否则为站姿
    """ 
    if angle_Lbody <160 and angle_Rbody< 160:
        body_pose ="坐姿"
    return body_pose
""" 
判断是半身还是全身还是
""" 
def body_part(results,img):
    body_check="全身"
    # 检测脚步关节点是否可见
    Lankle=results.pose_landmarks.landmark[27]
    Rankle=results.pose_landmarks.landmark[28]
    if Lankle.visibility< 0.5 and Rankle.visibility<0.5:
        body_check="大半身"
    return body_check

""" 计算三个关节点的角度 """
def calculate_angle_2d(a,b,c):
    a=np.array(a)
    b=np.array(b)
    c=np.array(c)

    radians=np.arctan2(c[1]-b[1],c[0]-b[0])-np.arctan2(a[1]-b[1],a[0]-b[0])
    angle =np.abs(radians*180.0/np.pi)

    if  angle >180.0:
        angle =360-angle
    return angle
# 检测完整性
def Iscomplete( results,img,body):
    landmark=results.pose_landmarks.landmark
    foot_visiblity =1 
    hand_visiblity=1
    if body=="全身":
    #   当膝盖的两个关节点检测出来时候，如果没有检测到脚的关节点，认为拍摄失误
        if  landmark[28].visibility> 0.5 or landmark[27].visibility>0.5 :
            if landmark[32].visibility<0.5 or landmark[31].visibility<0.5:
                foot_visiblity=0
    if  landmark[16].visibility> 0.5 or landmark[15].visibility>0.5 :
        if landmark[20].visibility<0.5 and landmark[21].visibility<0.5:
            hand_visiblity=0
    
    if(body=="全身"):
        if foot_visiblity==1 and hand_visiblity==1:
            Tips=""
        Tips="您的手或脚没有拍完整，请远离摄像头或改变姿势"
    else:
        if hand_visiblity==1:
            Tips=""
        Tips="您的手没有拍完整，请远离摄像头或改变手部姿势"

""" 
 计算两个图像的相似度

"""
def Compare_img(img1,img2):
    
    result1=Pose_recognition(img1)
    result2=Pose_recognition(img2)
    List1=Arm_legPose(result1)
    List2=Arm_legPose(result2)
    sum_XYSimlar=0  
    for i in range(0,len(List1)): 
        #两个数的欧几里得距离 
        XYdistiance=np.sqrt(np.sum(np.square(List1[i]-List2[i]))) 
        # #欧氏距离定义的相似度,距离越小相似度越大 
        print(XYdistiance)
        XYSimlar = 1/(1+XYdistiance) 
        #获取相似度和 
        sum_XYSimlar=sum_XYSimlar+XYSimlar 
        #获取两组数据相似度平均值 
    avg_XYSimlar=sum_XYSimlar/5 
    return  avg_XYSimlar
"""
输入两个三维向量，计算角度
"""

def calculate_angle_3d(a, b):
    cos_ab = a.dot(b) / (np.linalg.norm(a) * np.linalg.norm(b))
    return np.degrees(np.arccos(cos_ab))

"""
计算是否存在透视问题
"""

def Isperspective(results,img):
    point1 = [12, 14, 11, 13, 24, 26, 23, 25]
    point2 = [14, 16, 13, 15, 26, 28, 25, 27]
    degree = []
    # image = img
    # mp_pose = mp.solutions.pose
    # mpDraw = mp.solutions.drawing_utils
    # hoslitic = mp_pose.Pose(static_image_mode=True)
    # results = hoslitic.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    landmark = results.pose_landmarks.landmark
    # print(landmark[23], landmark[24], landmark[25],
    #       landmark[26], landmark[27], landmark[28])
    # if results.pose_landmarks:
    #     # 绘制姿态坐标点，img为画板，传入姿态点坐标，坐标连线
    #     mpDraw.draw_landmarks(img, results.pose_landmarks,
    #                           mp_pose.POSE_CONNECTIONS)
    # cv2.imwrite('images/ske.jpg', img)
    for i in range(8):
        if landmark[point1[i]].visibility < 0.2 or landmark[point2[i]].visibility < 0.2:
            degree.append(0)
        else:
            a = np.array([landmark[point1[i]].x - landmark[point2[i]].x, landmark[point1[i]].y -
                          landmark[point2[i]].y, landmark[point1[i]].z - landmark[point2[i]].z])
            b = np.array([landmark[point1[i]].x - landmark[point2[i]].x, landmark[point1[i]].y -
                          landmark[point2[i]].y, 0])
            degree.append(calculate_angle_3d(a, b))
    # print(degree)
    if(max(degree) > 75):
        return "存在透视"
    else:
        return ""

"""
计算是否存在合并问题
"""


def Ismerge(results,img):
    image = img
    # mp_pose = mp.solutions.pose
    # mpDraw = mp.solutions.drawing_utils
    # hoslitic = mp_pose.Pose(static_image_mode=True)
    # results = hoslitic.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    landmark = results.pose_landmarks.landmark
    # if results.pose_landmarks:
    #     # 绘制姿态坐标点，img为画板，传入姿态点坐标，坐标连线
    #     mpDraw.draw_landmarks(img, results.pose_landmarks,
    #                           mp_pose.POSE_CONNECTIONS)
    # cv2.imwrite('images/ske.jpg', img)

    for i in range(2):
        a = [landmark[14-i].x, landmark[14-i].y]
        b = [landmark[12-i].x, landmark[12-i].y]
        c = [landmark[24-i].x, landmark[24-i].y]
        angle = calculate_angle_2d(a, b, c)
        print(angle)
        Isout = Ispointontheoutside(a, b, c)
        if(i > 0):
            Isout = not Isout
        if (angle > 7.5 and angle < 30):
            if (Isout):
                if i == 0:
                    return "左侧存在合并问题"
                else:
                    return "右侧存在合并问题"
    return ""


"""
判断点与直线的位置关系(直线由后两个点确定)
"""
def Ispointontheoutside(a, b, c):
    # print((b[1]-c[1])/(b[0]-c[0]))
    return (a[1] - a[0]*(b[1]-c[1])/(b[0]-c[0])-b[1]+(b[1]-c[1])/(b[0]-c[0])*b[0])*(b[1]-c[1])/(b[0]-c[0]) > 0


