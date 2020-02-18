const jwt=require('jsonwebtoken')
const config =require('../util/config')
const url = require('url');
const auth =require('../model/authorization')
 const utils=require('./utils')



const authorization= async (request,response,next)=>
{
    console.log("url" ,url.parse(request.url).pathname);
    if(!config.excludeUrl.includes(url.parse(request.url).pathname)){
    const authorization=request.get("authorization");
    let token=null;
    if(authorization&& authorization.toLowerCase().startsWith('bearer ') )
    {
        try{
        token=authorization.substring(7)

        token=await utils.verifyToken(token,"login");
            console.log("token ",token);

        if(token && token.id )
        {
            response.locals.user_id=token.id;
            response.locals.user_name=token.name;
            response.locals.key=authorization.substring(7);
            next()  
        }
        else if(token==="token-expired")
        {
            response.status(401).json({message:"token exipred"}).send()
        }
        else if(token==="token-invalid")
        {
            response.status(401).json({message:"token invalid"}).send()
        }
        
    }
    catch(exp)
    {
        response.status(401).json({message:"exception while signing out "+exp}).send()
    }
    }
    else
    {
        response.status(401).json({message:"authorization token not found"}).send()
    }
}
else
{
    next()
}
    

}

module.exports=authorization