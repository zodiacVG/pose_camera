import cv2
from matplotlib import pyplot as plt
import numpy as np


def get_output_layers(net):

    layer_names = net.getLayerNames()

    output_layers = [layer_names[i - 1] for i in net.getUnconnectedOutLayers()]

    return output_layers

# function to draw bounding box on the detected object with class name
#h画框框的函数
def draw_bounding_box(classes, COLORS, img, class_id, confidence, x, y, x_plus_w, y_plus_h):

    #claseeid:在分类表中的id
    label = str(classes[class_id])

    color = COLORS[class_id]

    cv2.rectangle(img, (x, y), (x_plus_w, y_plus_h), color, 2)

    cv2.putText(img, label, (x-10, y-10),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)
# function to get the output layer names
# in the architecture

# 读入的image是cv2解析之后的


def ThirdRulesDetection(image):
    
    Width = image.shape[1]
    Height = image.shape[0]
    scale = 1/255
    # 读取分类表
    classes = None

    #四个完美点的位置，从左至右，从上往下
    third_rule_point_x1_x = Width/3
    third_rule_point_x1_y = Height/3
    
    third_rule_point_x2_x = (Width/3)*2
    third_rule_point_x2_y = Height/3

    third_rule_point_x3_x = Width/3
    third_rule_point_x3_y = (Height/3)*2

    third_rule_point_x4_x = (Width/3)*2
    third_rule_point_x4_y = (Height/3)*2

    #使用二维数组存放四个标准点
    third_rule_points = [[]]

    third_rule_points[0] = [third_rule_point_x1_x,third_rule_point_x1_y]
    third_rule_points[1] = [third_rule_point_x2_x,third_rule_point_x2_y]
    third_rule_points[2] = [third_rule_point_x3_x,third_rule_point_x3_y]
    third_rule_points[3] = [third_rule_point_x4_x,third_rule_point_x4_y]

    

    with open("H:/github/camera_project_files/cv_dnn_models/yolov3.txt", 'r') as f:
        classes = [line.strip() for line in f.readlines()]

    # generate different colors for different classes
    COLORS = np.random.uniform(0, 255, size=(len(classes), 3))

    # 导入模型
    net = cv2.dnn.readNet("H:/github/camera_project_files/cv_dnn_models/yolov3.weights",
                          "H:/github/camera_project_files/cv_dnn_models/yolov3.cfg")
    # model = cv2.dnn_DetectionModel(net)
    # 压缩图片至DNN可处理的尺寸，尺寸越大检测效果越好，但处理速度会变慢
    # model.setInputParams(size=(320, 320), scale=1 / 255)

    print(net)
    blob = cv2.dnn.blobFromImage(
        image, scale, (416, 416), (0, 0, 0), True, crop=False)

    # set input blob for the network
    net.setInput(blob)

    # run inference through the network
    # and gather predictions from output layers
    outs = net.forward(get_output_layers(net))

    # initialization
    class_ids = []
    confidences = []
    boxes = []
    conf_threshold = 0.5
    nms_threshold = 0.4

    # for each detetion from each output layer
    # get the confidence, class id, bounding box params
    # and ignore weak detections (confidence < 0.5)
    #获取置信度较高的识别出来的数据，位置，类别等等 
    for out in outs:
        for detection in out:
            scores = detection[5:]
            class_id = np.argmax(scores)  #最大的分数值
            confidence = scores[class_id]  #置信度是多少
            if confidence > 0.5:
                center_x = int(detection[0] * Width)
                center_y = int(detection[1] * Height)
                w = int(detection[2] * Width)
                h = int(detection[3] * Height)
                x = center_x - w / 2
                y = center_y - h / 2
                class_ids.append(class_id)
                confidences.append(float(confidence))
                boxes.append([x, y, w, h])
    # apply non-max suppression
    indices = cv2.dnn.NMSBoxes(boxes, confidences, conf_threshold, nms_threshold)

    # go through the detections remaining
    # after nms and draw bounding box
    for i in indices:
        i = i
        box = boxes[i]
        x = box[0]
        y = box[1]
        w = box[2]
        h = box[3]

        #todo 如果标签是person的话，就开始判断是不是在完美点上


        #x，y轴坐标，长度和宽度
        print(x,y,w,h)

        draw_bounding_box(classes, COLORS, image, class_ids[i], confidences[i], round(x), round(y), round(x+w), round(y+h))


    # display output image    
    cv2.imshow("object detection", image)

    # wait until any key is pressed
    cv2.waitKey()

     # save output image to disk
    cv2.imwrite("object-detection.jpg", image)

    # release resources
    cv2.destroyAllWindows()

    return 'call second file'
