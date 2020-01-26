
const signupRouter = require('express').Router()
const User = require('../../model/user')
const md5=require('md5')

signupRouter.post('/', async (request, response) => {
  try {
    const body = request.body
    if(body.user_name && body.password && body.email)
    {
    const passwordHash = body.password

    const user = new User({
      name: body.user_name,
      email:body.email,
      passwordHash:md5(passwordHash),
    })

    const savedUser = await user.save()

    response.json(savedUser.toJSON())
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