# coding="utf-8"
from flask import Flask

# create a MyFlask app
app = Flask(__name__)

@app.route("/")
def index():
    return "Hello world !"

if __name__ == '__main__':
    # 默认访问：http://127.0.0.1:8888/
    app.run(debug=True, host='0.0.0.0', port=8888)
