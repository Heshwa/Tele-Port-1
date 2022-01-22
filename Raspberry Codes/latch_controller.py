#!/usr/bin/python
from flask import Flask
import RPi.GPIO as GPIO
from time import sleep
 
Door=17
app = Flask(name)
GPIO.setmode(GPIO.BCM)
GPIO.setup(Door, GPIO.OUT)

@app.route('/')
def index():
    return 'Hello world'


@app.route('/opendoor')
def openDoor():
    GPIO.output(Door, GPIO.LOW)
    res = {}
    res['status'] = True
    res['output'] = 'Open Door'

    return res

@app.route('/closedoor')
def closeDoor():
    GPIO.output(Door, GPIO.HIGH)
    res ={}
    res['status'] =True
    res['output'] ='Close Door'

    return res

if name == 'main':
    app.run(debug=True,host='0.0.0.0')