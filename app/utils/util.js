const multer=require('multer')
const File=require('../models/uploads')


var storage = multer.diskStorage({
    destination: function (req, file, cb) {
    cb(null, 'uploads')
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + '-' +file.originalname )
  }
})

var upload = multer({ storage: storage }).single('file')

const addFile=async (name,format,user_id,private_key)=>
{
    try{
        const file=new File({name:name,format:format,user_id:user_id,private_key:private_key})
        const savedFile=await file.save()
        return savedFile

    }
    catch(exp)
    {
        console.log("exception while adding file ",exp);
    }
}


const getFiles=async()=>
{
let files=await File.find({});
files=files.map(file=>file.toJSON())
return files;
}

module.exports={upload,addFile,getFiles};