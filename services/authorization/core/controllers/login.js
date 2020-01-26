const loginRouter = require('express').Router()
const User = require('../../model/user')
const md5=require('md5')
const jwt=require('jsonwebtoken')
const Auth=require('../../model/authorization')



loginRouter.post('/',async (req,res)=>{
    try{
    const body=req.body;
    if(body.user_name && body.password)
    {
        const user=await User.findOne({name:body.user_name})
        if(user)
        {   console.log("user =>",user);
            if(user.passwordHash===md5(body.password))
            {
            const userForToken = {
                name: user.name,
                id: user._id,
              }
            
            let token=jwt.sign(userForToken,process.env.SECRET)
            const splitToken=token.split(".")
            console.log("splitToken =>",splitToken);
            const auth =new Auth({key:splitToken[2],payload:splitToken[1]});
           //token = await Auth.add({key:splitToken[2],payload:splitToken[1]}).toJSON();
           token=await auth.save();
           token=token.toJSON();

            res.status(200).json({token:token.key,username:user.name,id:user._id}).send()
            }
            else
            {
                res.status(401).json({message:"user not found or invalid username/password"}).send() 
            }
        }
        else
        {
            res.status(401).json({message:"user not found or invalid username/password"}).send()
        }


    }
    else
    {
        res.status(400).json({message:"incorrect data"}).send()
    }
}
catch(exp)
{
    res.status(500).json({message:"exception while signing-in"}).send()
    console.log("exception while logining ",exp);
}
})

module.exports=loginRouter