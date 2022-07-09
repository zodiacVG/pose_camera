import base64
# from socket import if_nameindex
from sqlite3 import Time
import string
import pose
from flask import Flask, request 
import mediapipe as mp
import cv2
import numpy as np
import logging
app = Flask(__name__)
mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles
mp_pose = mp.solutions.pose



@app.route('/', methods=['POST','GET'])  
def Getimg():
    img = request.form['base64']
    with open ('1.jpg','wb')as files:
        imgdata = base64.b64decode(img)
        files.write(imgdata)
    image =cv2.imread('1.jpg')
    Pose_results =pose.Pose_recognition(image)
    if Pose_results.pose_landmarks is None:
        return ""
    # 判断半身还是全身
    body_part=pose.body_part(Pose_results,image)
    # 判断手脚完整性
    pose_complete=pose.Iscomplete(Pose_results,image,body_part)
    # 判断是站姿还是坐姿
    isstand=pose.Sit_or_stand(Pose_results,image)  
    ismerge=pose.Ismerge(Pose_results,image)
    isperspective=pose.Isperspective(Pose_results,image)
    result=isstand
    if ismerge.find("存在")!=-1:
        result=result+str("/")+ismerge
    if isperspective.find("存在")!=-1:
        result=result+str("/")+isperspective
    return result
    # res=Iscomplete(image)
    # return res
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

