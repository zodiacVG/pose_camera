import cv2
from matplotlib import pyplot as plt
import numpy as np

#读入的image是cv2解析之后的
def ThirdRulesDetection(image):
    # 读取分类表
    classes = None
    with open("H:/github/camera_project_files/cv_dnn_models/yolov3.txt", 'r') as f:
        classes = [line.strip() for line in f.readlines()]
    
    # generate different colors for different classes 
    COLORS = np.random.uniform(0, 255, size=(len(classes), 3))
    
    net = cv2.dnn.readNet("H:/github/camera_project_files/cv_dnn_models/yolov3.weights",
                      "H:/github/camera_project_files/cv_dnn_models/yolov3.cfg")    # 导入模型
    model = cv2.dnn_DetectionModel(net)
    model.setInputParams(size=(320, 320), scale=1 / 255)  # 压缩图片至DNN可处理的尺寸，尺寸越大检测效果越好，但处理速度会变慢
    


    return 'call second file'

