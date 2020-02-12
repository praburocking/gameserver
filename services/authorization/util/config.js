require('dotenv').config()

let MONGODB_URL = process.env.MONGODB_URL
const excludeUrl=['/login','/signup','/pay/stripe/webhook','/signup/exist'];
let ENVIRONMENT=process.env.ENVIRONMENT
let SECURITY_HEADER=process.env.SECURITY_HEADER
let SECRET=process.env.SECRET


module.exports = {
  MONGODB_URL,
 excludeUrl,
 ENVIRONMENT,
 SECURITY_HEADER,
 SECRET
}
