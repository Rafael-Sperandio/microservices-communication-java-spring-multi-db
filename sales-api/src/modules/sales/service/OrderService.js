import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../products/rabbitmq/productStockUpdateSender.js";
import { PENDING } from "../status/OrderStatus.js";

import {
  BAD_REQUEST,
  SUCCESS,
  INTERNAL_SERVER_ERROR,
} from "../../../config/constants/httpStatus.js";
import OrderException from "../exception/OrderException.js";
import ProductClient from "../../products/client/productClient.js";

class OrderService{
    async createOrder(req) {
        try{
            let orderData = req.body;
            this.validateOrderData(orderData);
            const { authUser } = req;
            const { authorization } = req.headers;
            let order = this.createInitialOrderData(orderData,authUser);
            await this.validateProductStock(order,authorization);
            let createdOrder = await OrderRepository.save(order);
            this.sendMessage(createdOrder)
            return {
                status: SUCCESS,
                createdOrder,
            };
            
        }catch(err) {
            return {
                status: err.status ? err.status : INTERNAL_SERVER_ERROR,
                message: err.message,
            };
        }
    }

    createInitialOrderData(orderData, authUser
        ) {
        return {
            status: PENDING,
            user: authUser,
            createdAt: new Date(),
            updatedAt: new Date(),
            products: orderData.products,
        };
    }

    //atualiza o stauts da order
    async updateOrder(orderMessage) {
        try {
        const order = JSON.parse(orderMessage);
            if(order.salesId && order.status) {
                let existingOrder = await OrderRepository.findById(order.salesId);
                if (existingOrder && order.status !== existingOrder.status) {
                    existingOrder.status = order.status;
                    existingOrder.updatedAt = new Date();
                    await OrderRepository.save(existingOrder);
                }
            } else {
                console.warn(`The order message was not complete.`);
            }
        } catch (err) {
            console.error("Could not parse order message from queue.");
            console.error(err.message);
        }
    }

    validateOrderData(data) {
        if (!data || !data.products) {
        throw new OrderException(BAD_REQUEST, "The products must be informed.");
        }
    }
    
    async validateProductStock(order
        , token
    ) {
        let stockIsOk = await ProductClient.checkProducStock(
        order,
        token
        );
        if (!stockIsOk) {
        throw new OrderException(
            BAD_REQUEST,
            "The stock is out for the products."
        );
        }
    }
    
    sendMessage(createdOrder) {
        const message = {
            salesId: createdOrder.id,
            products: createdOrder.products,
        };
        sendMessageToProductStockUpdateQueue(message);
    }

    async findById(req) {
        try {
        const { id } = req.params;
        this.validateInformedId(id);
        console.info(
            `Request to GET order by ID ${id}.`
        );
        const existingOrder = await OrderRepository.findById(id);
        if (!existingOrder) {
            throw new OrderException(BAD_REQUEST, "The order was not found.");
        }

        let response = {
            status: SUCCESS,
            existingOrder,
        };

        console.info(
            `Response to GET order by ID ${id}: ${JSON.stringify(response)}.`
        );
        return response;
        } catch (err) {
            return {
                status: err.status ? err.status : INTERNAL_SERVER_ERROR,
                message: err.message,
            };
        }
    }

    async findAll(req) {
        try {
        //const { transactionid, serviceid } = req.headers;
        console.info( `Request to GET all orders`);
        const orders = await OrderRepository.findAll();
        if (!orders) {
            throw new OrderException(BAD_REQUEST, "No orders were found.");
        }
        let response = {
            status: SUCCESS,
            orders,
        };
        console.info(
            `Response to GET all orders: ${JSON.stringify(response)}.`
        );
        return response;
        } catch (err) {
        return {
            status: err.status ? err.status : INTERNAL_SERVER_ERROR,
            message: err.message,
        };
        }
    }
    
    async findbyProductId(req) {
        try {
        const { productId } = req.params;
        console.info(
            `Request to GET orders by productID ${productId}`
        );
        this.validateInformedProductId(productId);
        const orders = await OrderRepository.findByProductId(productId);
        if (!orders) {
            throw new OrderException(BAD_REQUEST, "No orders were found.");
        }
        let response = {
            status: SUCCESS,
            salesIds: orders.map((order) => {
            return order.id;
            }),
        };
        console.info(
            `Response to GET orders by productID ${productId}: ${JSON.stringify(
            response)}.`
        );
        return response;
        } catch (err) {
        return {
            status: err.status ? err.status : INTERNAL_SERVER_ERROR,
            message: err.message,
        };
        }
    }


    validateInformedId(id) {
        if (!id) {
            throw new OrderException(BAD_REQUEST, "The order ID must be informed.");
        }
    }

    validateInformedProductId(id) {
        if (!id) {
            throw new OrderException(
                BAD_REQUEST,
                "The order's productId must be informed."
            );
        }
    }

    
}
export default new OrderService();