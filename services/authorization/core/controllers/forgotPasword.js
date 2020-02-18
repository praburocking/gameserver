const forgotPassRouter = require('express').Router()
const User = require('../../model/user')
const utils =require('../utils')
const config=require('../../util/config')
const resetPasswordRouter = require('express').Router()
const md5=require('md5')




forgotPassRouter.post('/',async (req,res)=>{
    try{
    const body=req.body;
    if(body.email)
    {
        const user=await User.findOne({email:body.email})
        console.log("user",user);
        if(user && user._id)
        {
            let token= await utils.issueToken(user,60*15,"revivePassword")
            console.log("token",token);
            const link=config.SERVER_URL+"/resetpassword?token="+token.key;
            utils.forgotPasswordMail(user.email, user.name,link);
            res.status(200).json({message:"password recovery key is sent to your mail"});

        }
        else
        {
            res.status(400).json({message:"user not found"}).send()
        }
    }
    else
    {
        res.status(400).json({message:"incorrect data"}).send()
    }
}
catch(exp)
{
    res.status(500).json({message:"exception while generating password reviving link"}).send()
    console.log("exception while logining ",exp);
}
})

forgotPassRouter.post('/verifykey',async(req,res)=>{
    try{
        let token=req.query.token;
        let password=req.body.password;
        console.log("verify key",token,password);
        token=await utils.verifyToken(token,"revivePassword",false);
        console.log("token3",token);
        if(token && typeof token==="object")
        {
        res.status(200).json({email:token.email,status:true});
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
    catch(exp)
    {
        res.status(500).json({message:"exception while verifying"}).send()
        console.log("excption ",exp);
    }
})

resetPasswordRouter.post('/',async(req,res)=>{
    console.log("resetting pass",req.body);
let body=req.body
try{
if(body.token && body.password )
{
    let token=await utils.verifyToken(body.token,"revivePassword",true);
    console.log("token3",token);
    if(token && typeof token==="object")
    {
        let user= await User.findOne({_id:token.id});
        console.log("user before passchange",user);
        user= await User.update({_id:token.id} ,{passwordHash:md5(body.password)});
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



module.exports={forgotPassRouter,resetPasswordRouter}