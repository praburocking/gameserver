require('dotenv').config()

let MONGODB_URL = process.env.MONGODB_URL
const excludeUrl=['/login','/signup','/pay/stripe/webhook','/signup/exist','/forgotpassword','/forgotpassword/verifykey','/resetpass','/verifyuser'];
let ENVIRONMENT=process.env.ENVIRONMENT
let SECURITY_HEADER=process.env.SECURITY_HEADER
let SECRET=process.env.SECRET;
let SERVER_URL=process.env.SERVER_URL;


module.exports = {
  MONGODB_URL,
 excludeUrl,
 ENVIRONMENT,
 SECURITY_HEADER,
 SECRET,
 SERVER_URL
}
