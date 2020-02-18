
const User = require('../../model/user')
const utils =require('../utils')
const config=require('../../util/config')
const verifyUserRouter = require('express').Router()
const md5=require('md5')




verifyUserRouter.post('/',async(req,res)=>{
    console.log("resetting pass",req.body);
let body=req.body
try{
if(body.token  )
{
    let token=await utils.verifyToken(body.token,"verifyUser",true);
    if(token && typeof token==="object")
    {
        let user= await User.findOne({_id:token.id});
         user= await User.update({_id:token.id} ,{isVerified:true});
        console.log("user after passchange",user);
        if(user)
        {
            res.status(200).json({email:token.name,status:true});
        }
        else
        {
            res.status(500).json({message:"exception while updating password"}).send()
        }
    }
    else if(token && typeof token==='string')
    {
        if(token==='token-expired')
        {
            res.status(400).json({message:"token expired"}).send()
        }
        else if(token==='token-invalid')
        {
            res.status(400).json({message:"token invalid"}).send()
        }
        else
        {
            res.status(400).json({message:"exception while validating"}).send()  
        }
    }
    else
    {
        res.status(400).json({message:"exception while validating"}).send()     
    }
}
else
{
    res.status(400).json({message:"params missing"}).send()    
}
}
catch(exp)
{
    res.status(500).json({message:"exception while resetting password"}).send();
    console.log("exp ",exp)
}

})



module.exports=verifyUserRouter;