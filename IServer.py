import os
import json
from flask import Flask, request,url_for,redirect,flash,send_from_directory,render_template,abort,Response,jsonify,g
from createusertable import User,base
from createpictable import pics,picbase
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from werkzeug.utils import secure_filename
from datetime import datetime
from functools import wraps
from flask_httpauth import HTTPBasicAuth





exts = set(['jpg','png','jpeg','gif'])  # Supported Formats....Also supports .txt or .pdf formats
ip = "YOUR MACHINE's IP Address"


UPLOAD_FOLDER = 'UPLOAD FOLDER ADDRESS HERE' #Upload folder is in same directory as IServer.py
USER_FOLDER = 'UPLOAD FOLDER ADDRESS HERE' #Upload folder is in same directory as IServer.py
myapp = Flask(__name__)
myapp.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
myapp.secret_key = 'abcxyz123'

engine = create_engine(r'sqlite:///users.db')
base.metadata.bind = engine
dbsession = sessionmaker(engine)
session = dbsession()

engine = create_engine(r'sqlite:///picdata.db')
picbase.metadata.bind = engine
dbsession = sessionmaker(engine)
picsession = dbsession()

auth = HTTPBasicAuth()


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.',1)[1].lower() in exts


def upload_file(request):
    try:
        
        if request.method == 'POST':
            if 'user_image' not in request.files:
                return 'no file'

            file = request.files['user_image']

            if file.filename == '':
                return 'empty filename'

            if file and allowed_file(file.filename):
                filename = secure_filename(request.form['username']+'.jpg')
                file.save(os.path.join(USER_FOLDER,filename))
                
                return ip+url_for('display_userpics',filename = filename)
    except Exception as e:
        return e

@myapp.route('/upload_pic',methods = ['POST'])
def upload_pics():
    try:
        
            if 'post_pic' not in request.files:
                return 'no file'

            file = request.files['post_pic']

            if file.filename == '':
                return 'empty filename'

            if file and allowed_file(file.filename):
                filename = secure_filename(file.filename)
                file.save(os.path.join(myapp.config['UPLOAD_FOLDER'],filename))
                picuser = session.query(User).filter_by(token = request.form['token']).first()
                i = datetime.now()
                if (request.form['caption']==''):
                    picup = pics(imagename = os.path.splitext(filename)[0],url = ip+url_for('display_uploaded',filename = filename),time = i.strftime('%d %b at %I:%M %p'),user = picuser.username,likes = 0)
                else:
                    picup = pics(imagename = os.path.splitext(filename)[0],url = ip+url_for('display_uploaded',filename = filename),time = i.strftime('%d %b at %I:%M %p'),user = picuser.username,caption = request.form['caption'],likes = 0)            
                picsession.add(picup)
                picsession.commit()
                return 'Uploaded Successfully'
    except Exception as e:
        return 'Exception'


@myapp.route('/uploads/<filename>')
def display_uploaded(filename):
        return send_from_directory(myapp.config['UPLOAD_FOLDER'],filename)

@myapp.route('/users_folder/<filename>')
def display_userpics(filename):
        return send_from_directory(USER_FOLDER,filename)


@myapp.route('/new_user',methods = ['POST'])
def new_user():
    picuploaded = False
    username = request.form['username']
    password = request.form['password']
    email = request.form['email']
    phone = request.form['phone']
    if username is None or password is None or email is None:
        return 'None of the field can be empty' 
    if session.query(User).filter_by(username = username).first() is not None:
        return 'User already exists with same Username'
    pic = upload_file(request)
    if (not pic == 'Not done'):
        user = User(username = username,email = email,phone = phone,pic = pic,token = "hello")
    else:
     user = User(username = username,email = email,phone = phone,token = "hello")
    user.hash_password(password)
    session.add(user)
    session.commit()
    session1 = dbsession()
    if verify_user(request):
        user = session1.query(User).filter_by(username = username).first()
        token = user.generate_auth_token(session1,request)
        return token
    else:
        return 'Error creating user'








def verify_user(request):
    username = request.form['username']
    password = request.form['password'] 
    user = session.query(User).filter_by(username = username).first()
    if not user or not user.verify_password(password):
        return False 
    return True
    
    

@myapp.route('/uploads',methods = ['GET','POST'])
def display_uploaded_files():
    if (User.verify_auth_token(request.form['token'],session)):
        user = session.query(User).filter_by(token = request.form['token']).first()
        piclist = picsession.query(pics).all()
        piclist.reverse()
        return json.dumps([{'userpic': user.pic},[{'name': row.caption, 'url':row.url, 'user' : row.user, 'pic_userpic': ip+url_for('display_userpics',filename = row.user+'.jpg'),'time': row.time , 'likes' : row.likes , 'imgname' :  row.imagename } for row in piclist]],indent = 4)




      

    else:
        return 'Not Logged In'
    

    
@myapp.route('/like',methods = ['POST'])
def likeit():
    name = request.form['imagename']
    pic = picsession.query(pics).filter_by(imagename = name).first()
    pic.likes = pic.likes+1
    total = pic.likes
    picsession.add(pic)
    picsession.commit()
    return str(total)

@myapp.route('/unlike',methods = ['POST'])
def unlikeit():
    name = request.form['imagename']
    pic = picsession.query(pics).filter_by(imagename = name).first()
    pic.likes = pic.likes-1
    total = pic.likes
    picsession.add(pic)
    picsession.commit()
    return str(total)

    
@myapp.route('/login',methods = ['POST'])
def login():
    if verify_user(request):
        user = session.query(User).filter_by(username = request.form['username']).first()
        token = user.generate_auth_token(session,request)
        
        return token
    else:
        return "Not Found"
        
    
    



if __name__ == '__main__':
    myapp.run(host = '0.0.0.0',port = 2222,debug = True)
    
