
import mediapipe as mp
import cv2
import numpy as np
import logging
from numpy.lib.function_base import angle
from scipy.spatial.distance import cdist

""" 计算三个关节点的角度 """
def calculate_angle(a,b,c):
    a=np.array(a)
    b=np.array(b)
    c=np.array(c)

    radians=np.arctan2(c[1]-b[1],c[0]-b[0])-np.arctan2(a[1]-b[1],a[0]-b[0])
    angle =np.abs(radians*180.0/np.pi)

    if  angle >180.0:
        angle =360-angle
    return angle
""" 
    传图像进行姿势识别
    并计算左右胳膊和左右腿的角度赋给list中
"""
def Pose_recognition(img):
    image=img
    height,width,_ = image.shape
    mp_pose = mp.solutions.pose
    hoslitic= mp_pose.Pose(
        static_image_mode=True)
    results = hoslitic.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    """ 把每一个关节点的左右关节点的x,y坐标取出来 """
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
    angle_Rarm= calculate_angle(Lshoulder,Lelbow,Lwrist) 
    angle_Larm= calculate_angle(Rshoulder,Relbow,Rwrist)
    angle_Rleg= calculate_angle(Lhip,Lknee,Lankle)
    angle_Lleg= calculate_angle(Rhip,Rknee,Rankle)
    array = np.array([angle_Rarm,angle_Larm,angle_Rleg,angle_Lleg])
    return array

""" 
 计算两个图像的相似度

"""

def Iscomplete(img):
    foot_visiblity=1
    image=img
    mp_pose = mp.solutions.pose
    mpDraw = mp.solutions.drawing_utils

    hoslitic= mp_pose.Pose(
        static_image_mode=True)
    results = hoslitic.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    if results.pose_landmarks:
        
        # 绘制姿态坐标点，img为画板，传入姿态点坐标，坐标连线
        mpDraw.draw_landmarks(img, results.pose_landmarks, mp_pose.POSE_CONNECTIONS)
    cv2.imshow('image', img)  
    cv2.waitKey(10000)
    landmark=results.pose_landmarks.landmark
    print(landmark[32])
    print(results.pose_landmarks.landmark[31])
    if  landmark[26].visibility> 0.7 or landmark[25].visibility>0.7 :
        if landmark[32].visibility<0.7  or landmark[31].visibility<0.7:
             foot_visiblity=0
    return foot_visiblity

def Compare_img(img1,img2):
    
    List1=Pose_recognition(img1)
    List2=Pose_recognition(img2)
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
    
if __name__=="__main__": 


    pose1=cv2.imread('images/pose2.jpeg')
    print(Iscomplete(pose1))
    # pose2=cv2.imread('images/pose2.jpeg')
    # similar=Compare_img(pose1,pose2)
    # print(similar)
  


