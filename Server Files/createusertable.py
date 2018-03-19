from sqlalchemy import Column,Integer, String, create_engine
from sqlalchemy.ext.declarative import declarative_base
from passlib.apps import custom_app_context as pwd_context
import uuid
from flask import jsonify
import json


base = declarative_base()



class User(base):
    __tablename__ = 'users'
    userid =  Column(Integer,autoincrement = True)
    username = Column(String(100),primary_key = True)
    password = Column(String(100),nullable = False)
    email = Column(String(100),nullable = False)
    phone = Column(Integer,nullable = False)
    pic = Column(String(100))
    token = Column(String(1000),nullable = False)
    


    def hash_password(self, password):
        self.password = pwd_context.encrypt(password)

    def verify_password(self, password):
        return pwd_context.verify(password, self.password)

    def generate_auth_token(self,session,request):
        token = str(uuid.uuid4())
        user = session.query(User).filter_by(username = request.form['username']).first()
        user.token = token
        session.add(user)
        session.commit()
        return token

    @staticmethod
    def verify_auth_token(token, session):
        if (session.query(User).filter_by(token = token).first()):
                return True
        else:
                return False
        
    

engine = create_engine('sqlite:///users.db')

base.metadata.create_all(engine)





    
