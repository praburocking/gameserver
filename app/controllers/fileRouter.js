const fileRouter = require('express').Router()
//const User = require('../../model/user')
// const multer=require('multer')
const util=require('../utils/util')
const File =require('../models/uploads')
var fs = require('fs');
const stream=require('stream')



fileRouter.post('/upload',async (req,res)=>{
    try{
       
        const upload=req.files.file;
       let file= await util.encAndaddFile(upload,req.body.key,res.locals.user_id);
        res.status(200).json(file);
    }
    catch(exp)
    {
        res.status(500).json({message:"exception while writing your file"}).send()
        console.log("exception while writing the file ",exp);
    }
    })


fileRouter.get('/list',async(req,res)=>{
    try{
       const files= await util.getFiles(res.locals.user_id);
       res.status(200).json([...files]).send();
    }
    catch(exp)
    {
        res.status(500).json({message:"exception while getting files list"});
        console.log("exception while getting the file ",exp);
    }
})


fileRouter.post('/download/:id',async(req,res)=>{
    try{
        console.log("id ",req.params.id);
      const id=req.params.id;
      const file=await File.findOne({_id:id})
      if(file && res.locals.user_id===file.user_id)
      {    
          console.log("format ",file.format);
          console.log("req body",req.body);
          fileContent=await util.downloadFile('uploads/'+file._id+'.enc',req.body.key+file.private_key,res,file);
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
      {   try{ 
           await fs.unlinkSync('uploads/'+id+'.enc')
            }
            catch(exp)
            {
                console.log("exception while getting the file ",exp);
            }

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