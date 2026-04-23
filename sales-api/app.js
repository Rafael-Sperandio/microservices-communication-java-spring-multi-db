import express from "express";
import { connectMongoDb } from "./src/config/db/MongoDbConfig.js";
import Order from "./src/modules/sales/models/Order.js";
import {createInitalData} from "./src/config/db/initialData.js"
import { connectRabbitMq} from "./src/config/rabbitmq/rabbitConfig.js"

//import { sendMessageToProductStockUpdateQueue } from "./src/modules/products/rabbitmq/productStockUpdateSender.js";

import checkToken from "./src/config/auth/checkToken.js";
import router from "./src/modules/sales/routes/OrderRoutes.js"

const app = express();
const env = process.env
const PORT = env.PORT || 8082;

connectMongoDb();
createInitalData();
connectRabbitMq();

app.use(express.json())
app.use(checkToken);
app.use(router);
/*
app.get('/teste',async (req,res)=>{
    try{
        sendMessageToProductStockUpdateQueue(
            [
                {
                "productId": 1001,
                "quantity": 4
                },
                {
                "productId": 1002,
                "quantity": 2
                },
                {
                "productId": 1003,
                "quantity": 1
                }
            ]
        )
        return res.status(200).json({status: 200})
    }catch(err){
        console.log(err)
        return res.status(500).json({error:true});
    }
    // let teste = await Order.find();
    // console.log("teste : ",teste);

    
})
//*/

app.get('/api/status',async (req,res)=>{
    return res.status(200).json({
        service: 'Sales-API',
        status: 'up',
        httpstatus: 200,
    })
})


app.listen( PORT, () =>{
console.info(`server started sucessfully at port ${PORT}`)
})
