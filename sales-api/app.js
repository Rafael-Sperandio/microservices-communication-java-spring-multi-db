import express from "express";
import { connectMongoDb } from "./src/config/db/MongoDbConfig.js";
import Order from "./src/modules/sales/models/Order.js";
import {createInitalData} from "./src/config/db/initialData.js"
import { connectRabbitMq} from "./src/config/rabbitmq/rabbitConfig.js"

import checkToken from "./src/config/auth/checkToken.js";

const app = express();
const env = process.env
const PORT = env.PORT || 8082;

connectMongoDb();
createInitalData();
connectRabbitMq();

app.use(checkToken);

app.get('/api/status',async (req,res)=>{
    // let teste = await Order.find();
    // console.log("teste : ",teste);

    return res.status(200).json({
        service: 'Sales-API',
        status: 'up',
        httpstatus: 200,
    })
})


app.listen( PORT, () =>{
console.info(`server started sucessfully at port ${PORT}`)
})
