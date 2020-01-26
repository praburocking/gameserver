const jwt=require('jsonwebtoken')
const config =require('./config')
const url = require('url');
const auth =require('../models/authentication')


const header="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
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
        
        let authData= await auth.find({key:token})//.map(data=>data.toJSON());
        authData=authData[0];

        if(authData)
        {
        token=header+"."+authData.payload+"."+token
        token=jwt.verify(token,config.SECRET)
        if(token && token.id)
        {
            response.locals.user_id=token.id;
            response.locals.user_name=token.name;
            response.locals.key=authData.key;
            next()
            
        }
        else
        {
            response.status(401).json({message:"Error while validating"}).send()
        }
    }
    else
    {
        response.status(401).json({message:"invalid token"}).send()
    }
    }
    catch(exp)
    {
        response.status(401).json({message:"invalid token"}).send()
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