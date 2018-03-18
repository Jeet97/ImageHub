from flask import Flask


ip = "YOUR MACHINE's IP Address"
exts = set(['jpg','png','jpeg','gif'])  # Supported Formats....Also supports .txt or .pdf formats
UPLOAD_FOLDER = 'UPLOAD FOLDER ADDRESS HERE' #Upload folder is in same directory as IServer.py
USER_FOLDER = 'UPLOAD FOLDER ADDRESS HERE' #Upload folder is in same directory as IServer.py



myapp = Flask(__name__)
myapp.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
myapp.secret_key = 'CHOOSE ANY SECRET KEY FOR SECURITY PURPOSES'
