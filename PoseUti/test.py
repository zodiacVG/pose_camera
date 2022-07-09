
import cv2
import numpy as np
import pose
import mediapipe as mp
import numpy as np

mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles
mp_pose = mp.solutions.pose

""" 
 计算两个图像的相似度

"""
def draw(results,img):
    annotated_image =img
    mp_drawing.draw_landmarks(
        annotated_image,
        results.pose_landmarks,
        mp_pose.POSE_CONNECTIONS,
        landmark_drawing_spec=mp_drawing_styles.get_default_pose_landmarks_style())
    cv2.imwrite( str("sit_annote") + '.png', annotated_image)

if __name__ == '__main__':
   image=cv2.imread('images/58.png')
   Pose_results =pose.Pose_recognition(image)
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
        # print(result)
        # print(isperspective)
   if isperspective.find("存在")!=-1:
        result=result+str("/")+isperspective
        print(isperspective)
   print(result)
    # str = 'a,hallo'
    # print(str.find('hello'))