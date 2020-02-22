require('dotenv').config()

let MONGODB_URL = process.env.MONGODB_URL
let ENVIRONMENT=process.env.ENVIRONMENT
let SERVER_URL=process.env.SERVER_URL;


module.exports = {
  MONGODB_URL,
 ENVIRONMENT,
 SERVER_URL
}
