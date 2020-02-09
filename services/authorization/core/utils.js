
const User = require('../model/user')
const md5=require('md5')
const jwt=require('jsonwebtoken')
const Auth=require('../model/authorization')

  const login=async (data)=>
{
    const user=await User.findOne({email:data.email,passwordHash:md5(data.password)})
        if(user)
        {   
            console.log("user =>",user);
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

           return ({token:token.key,username:user.name,id:user._id})
        }
        else
        {
         return  ({message:"user not found or invalid username/password"})
        }
}

module.exports={login}