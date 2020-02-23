const loginRouter = require('express').Router()
const utils =require('../utils')


loginRouter.post('/',async (req,res)=>{
    try{
    const body=req.body;
    console.log("login body",body);
    if(body.email && body.password)
    {
        const loginData=await utils.login({email:body.email,password:body.password},false);
        if(loginData.message)
        {
            res.status(401).json(loginData).send()
        }
        else
        {
            res.status(200).json(loginData).send()
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