
const signupRouter = require('express').Router()
const User = require('../../model/user')
const md5=require('md5')
const utils =require('../utils')

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
    })

    const savedUser = await user.save()
    if(savedUser && body.password && body.email)
    {
        const loginData=await utils.login({email:body.email,password:body.password})
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

module.exports = signupRouter