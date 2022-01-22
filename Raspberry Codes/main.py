import cv2 ,requests ,json ,pyrebase ,os
import datetime

USERNAME=""
PASSWORD=""
USERID=""
DOORID=""
i=0
def firebaseInitalization():
    firebaseConfig = {
        "apiKey": "",
        "authDomain": "",
        "projectId": "",
        "databaseURL": "",
        "storageBucket": "",
        "messagingSenderId": "",
        "appId": "",
        "measurementId": ""
    }
    firebase = pyrebase.initialize_app(firebaseConfig)
    return firebase

def authenticate_user(firebase):
    auth = firebase.auth()
    user = auth.sign_in_with_email_and_password(USERNAME, PASSWORD)
    user = auth.refresh(user['refreshToken'])
    return user





def sendRequest(url):
    URL = "https://fcm.googleapis.com/fcm/send"
    KEY = ""
    notification={
        "body": "Please take action",
        "title": "Someone came to your home",
        "image": url
    }
    body = {
        "to": "/topics/"+DOORID,
        "notification": notification

    }
    headers = {'content-type': 'application/json',
               "Authorization":"key="+KEY}
    r = requests.post(url=URL,headers=headers,data=json.dumps(body))
    print(r)

def uploadImage(firebase,user,filename):
    global i
    storage = firebase.storage()
    PATH = "screenshots/img"+str(i)+".jpg"
    i +=1
    if i==10:
        i=0

    storage.child(PATH).put(filename)
    url = storage.child(PATH).get_url(user['idToken'])

    db = firebase.database()
    # data = {"data":url}
    # data ={"imgUrl":url,"name":"Default User","St"}
    # db.child("image").remove()
    db.child("Users").child(USERID).child("Doors").child(DOORID).update({"image":url})
    return url



def main():
    filename = "image.jpg"
    firebase = firebaseInitalization()
    user =  authenticate_user(firebase)
    face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
    cap = cv2.VideoCapture(0)
    time = datetime.datetime.now()
    while True:
        _, img = cap.read()
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, 1.5, 4)
        for (x, y, w, h) in faces:
            cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)

        if len(faces)!=0:
            now = datetime.datetime.now()
            if(now-time).total_seconds()>30:
                cv2.imwrite(filename, img)
                url = uploadImage(firebase,user,filename)
                sendRequest(url)
                time = datetime.datetime.now()


        cv2.imshow('Portal', img)
        k = cv2.waitKey(30) & 0xff
        if k == 27:
            break
    cap.release()


if __name__ == '__main__':
    main()











