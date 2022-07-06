import base64
from sqlite3 import Time
import string
from flask import Flask, request 
import mediapipe as mp
import cv2
import numpy as np
import logging
app = Flask(__name__)
mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles
mp_pose = mp.solutions.pose
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
    

@app.route('/',methods=['GET','POST'])
def ProcessImg():

    img = request.files['file']
    imagefile ="images/test.jpg"
    img.save(imagefile)
    pose1=cv2.imread('images/pose.png')
    pose2=cv2.imread('images/pose2.jpeg')
    similar=Compare_img(pose1,pose2)
    confident = str(similar)
    return confident
# 检测完整性
def Iscomplete(img):
    image=img
    foot_visiblity=1
    mp_pose = mp.solutions.pose
    mpDraw = mp.solutions.drawing_utils
    hoslitic= mp_pose.Pose(
        static_image_mode=True)
    results = hoslitic.process(cv2.cvtColor(image,cv2.BGR2))
    landmark=results.pose_landmarks.landmark
#   当膝盖的两个关节点检测出来时候，如果没有检测到脚的关节点，认为拍摄失误
    if  landmark[26].visibility> 0.7 or landmark[25].visibility>0.7 :
        if landmark[32].visibility<0.7 or landmark[31].visibility<0.7:
             foot_visiblity=0
    if(foot_visiblity==1):
        return ""
    return "未检测到脚"

@app.route('/Iscomplete', methods=['POST','GET'])  
def Getimg():
    img = request.form['base64']
    with open ('1.jpg','wb')as files:
        imgdata = base64.b64decode(img)
        files.write(imgdata)
    image =cv2.imread('1.jpg')
    res=Iscomplete(image)
    return res
    # img_b64decode = base64.b64decode(img)  # base64解码
    # img_array = np.fromstring(img_b64decode,np.uint8) # 转换np序列
    # img=cv2.imdecode(img_array,cv2.COLOR_BGR2RGB)  # 


@app.route('/register', methods=['POST','GET'])
def register():
    print ( request.headers)
    print (request.form)
    print (request.form['name'])
    print (request.form.get('name'))
    print (request.form.getlist('name'))
    print (request.form.get('nickname', default='little apple'))
    return 'welcome'
 
if __name__ == '__main__':
    app.run(host='0.0.0.0',
      port= 8000,debug=True)   
    handler = logging.FileHandler('flask.log')
    app.logger.addHandler(handler)

