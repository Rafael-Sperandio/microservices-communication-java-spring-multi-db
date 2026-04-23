import axios from "axios";

import { PRODUCT_API_URL } from "../../../config/constants/secrets.js";
import { BAD_REQUEST } from "../../../config/constants/httpStatus.js";

class ProductClient {
  async checkProducStock(productsData, token) {
    try {
      const headers = {
        Authorization: token,
      };
      console.info(
        `Sending request to Product API with data: ${JSON.stringify(
          productsData
        )}.`
      );
      let response = false;
      await axios
        .post(
          `${PRODUCT_API_URL}/check-stock`,
          { products: productsData.products },
          { headers }
          
        )
        .then((res) => {
          console.info(
            `Success response from Product-API.`
          );
          response = true;
        })
        .catch((err) => {
          console.error(
            `Error response from Product-API.`
          );
          response = false;
        });
      return response;
    } catch (err) {
      console.error(
        `Error response from Product-API.`
      );
      return false;
    }
  }
}
export default new ProductClient();