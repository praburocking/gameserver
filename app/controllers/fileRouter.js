const fileRouter = require('express').Router()
//const User = require('../../model/user')
const multer=require('multer')
const util=require('../utils/util')
const File =require('../models/uploads')
var fs = require('fs');





fileRouter.post('/upload',async (req,res)=>{
    try{

        console.log("file1 => ",req.files.file);
        const upload=req.files.file;
        let file= await util.addFile(upload.name, upload.mimetype,res.locals.user_id,"test@1233",upload.size,upload.encoding,upload.md5,upload.truncated)
        file=file.toJSON()
        upload.mv('uploads/'+file.id, (error) => {
            if (error) {
                console.error(error);
                res.status(500).json('error while writing your file to disk');
            }
            else
            {
                res.status(200).json(file);
            }
          })
    }
    catch(exp)
    {
        res.status(500).json({message:"exception while writing your file"}).send()
        console.log("exception while writing the file ",exp);
    }
    })


fileRouter.get('/list',async(req,res)=>{
    try{
       const files= await util.getFiles();
       res.status(200).json([...files]).send();
    }
    catch(exp)
    {
        res.status(500).json({message:"exception while getting files list"});
        console.log("exception while getting the file ",exp);
    }
})


fileRouter.get('/download/:id',async(req,res)=>{
    try{
        console.log("id ",req.params.id);
      const id=req.params.id;
      const file=await File.findOne({_id:id})
      if(file && res.locals.user_id===file.user_id)
      {
          res.download('uploads/'+file._id,file.name);
      }

    }
    catch(exp)
    {
        res.status(500).json({message:"exception while getting files list"});
        console.log("exception while getting the file ",exp);
    }
})

fileRouter.delete('/:id',async(req,res)=>{
    try{
        console.log("id ",req.params.id);
      const id=req.params.id;
      const file=await File.deleteOne({_id:id,user_id:res.locals.user_id})
      if(file)
      {
          res.status(200).json({message:"file deleted"});
      }
      else
      {
        res.status(500).json({message:"file deleted"});
      }

    }
    catch(exp)
    {
        res.status(500).json({message:"exception while deleting files list"});
        console.log("exception while getting the file ",exp);
    }
})

module.exports=fileRouter