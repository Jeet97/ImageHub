from sqlalchemy import Column,Integer, String, create_engine
from sqlalchemy.ext.declarative import declarative_base



picbase = declarative_base()



class pics(picbase):
    __tablename__ = 'picdata'
    imagename = Column(String(100),primary_key = True)
    url = Column(String(100),nullable = False)
    time = Column(String(100),nullable = False)
    user = Column(String(100),nullable = False)
    caption = Column(String(1000))
    likes = Column(Integer)
    

    

engine = create_engine('sqlite:///picdata.db')

picbase.metadata.create_all(engine)
