require('dotenv').config()

let MONGODB_URL = process.env.MONGODB_URL
let ENVIRONMENT=process.env.ENVIRONMENT



module.exports = {
  MONGODB_URL,
  ENVIRONMENT

}
