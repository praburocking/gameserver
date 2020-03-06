
const signupRouter = require('express').Router()
const User = require('../../model/user')
const md5=require('md5')
const utils =require('../utils')
const mail=require('../../../notification/mail/mail')

signupRouter.post('/', async (request, response) => {
  try {
    const body = request.body
    if(  body.password && body.email)
    {
    const passwordHash = body.password

    const user = new User({
      name: body.user_name?body.user_name:body.email.split("@")[0],
      email:body.email,
      passwordHash:md5(passwordHash),
      isVerified:false,
      maxFileSize:100
    })

    const savedUser = await user.save()

    if(savedUser && body.password && body.email)
    { 
       utils.invitationMail(body.email,savedUser.name,savedUser.__id);
        const loginData=await utils.login({email:body.email,password:body.password},true);
        if(loginData.message)
        {
          response.status(400).json(loginData.message);
        }
        else
        {
          response.status(200).json(loginData);
        }
        
    }
    else
    {
      response.status(400).json({message:"cannot autologin please login manually"});
    }
    
  }
  else
  {
    response.status(400).json({messge:"invalid data"});
  }
  } catch (exception) {
   console.log("Excption ",exception);
   response.status(500).json({message:"internal error"});
  }

})


signupRouter.get('/exist',async(req,res)=>{
  console.log("request",req);

  if(req.query.email)
  {   
      console.log(req.query)
      const user=await User.findOne({email:req.query.email});
      if(user)
      { 
        res.status(200).json({status:true});
      }
      else
      {
        res.status(200).json({status:false});
      }
  }
  else
  {
    res.status(400).json({message:"invalid data"});
  }


})

module.exports = signupRouter