
import Order from "../../modules/sales/models/Order.js";

export async function createInitalData() {
    await Order.collection.drop();

    let firtsOrder=  await Order.create({
      products: [
        {
          productId: 1001,
          quantity: 2,
        },
        {
          productId: 1002,
          quantity: 1,
        },
        {
          productId: 1003,
          quantity: 1,
        },
      ],
      user: {
        id: "a1sd1as5d165ads1s6",
        name: "User Test",
        email: "teste@gmail.com",
      },
      status: "APPROVED",
      createdAt: new Date(),
      updatedAt: new Date(),
    });

    let secondOrder = await Order.create({
      products: [
        {
          productId: 1001,
          quantity: 4,
        },
        {
          productId: 1003,
          quantity: 2,
        },
      ],
      user: {
        id: "asd1as9d1asd1asd1as5d",
        name: "User Test 2",
        email: "usertest2@gmail.com",
      },
      status: "REJECTED",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
    let initialData = await Order.find();
    console.info(
      `Initial data was created: ${JSON.stringify(initialData, undefined, 4)}`
    );
}