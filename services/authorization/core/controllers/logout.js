const logoutRouter = require('express').Router()
const User = require('../../model/user')
// const md5=require('md5')
// const jwt=require('jsonwebtoken')
const Auth=require('../../model/authorization')



logoutRouter.post('/',async (req,res)=>{
    try{

        
        console.log("at logout ",res.locals)
        if(res.locals.key)
    {  
        const removeRes=await Auth.deleteOne({key:res.locals.key})
        res.status(200).json({message:"signed out"}).send()
    }
    else
    {
        res.status(400).json({message:"cannot logout authorization failed"}).send()
    }
}
catch(exp)
{
    res.status(500).json({message:"exception while loging-out"}).send()
    console.log("exception while logining ",exp);
}
})

module.exports=logoutRouter