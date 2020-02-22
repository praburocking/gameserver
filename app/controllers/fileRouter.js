const fileRouter = require('express').Router()
//const User = require('../../model/user')
const multer=require('multer')
const util=require('../utils/util')





fileRouter.post('/upload',async (req,res)=>{
    try{
       util.upload(req, res,async function (err) {
            if (err instanceof multer.MulterError) {
                return res.status(500).json(err)
            } else if (err) {
                return res.status(500).json(err)
            }
        console.log("file ==>",req.file);
        const file= await util.addFile(req.file.originalname, req.file.mimetype,res.locals.user_id,"test@1233")
       return res.status(200).json(file.toJSON());
 
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

module.exports=fileRouter