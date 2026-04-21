import bcrypt from "bcrypt";
import User from  "../../modules/user/model/User.js"
//import User from  "../../modules/user/model/User.js"
export async function createInitialData() {
    try {
        
        await User.sync({ force: true });
        
        const password = await bcrypt.hash("123456", 10);
        const firstUser = 
        await User.create({
            name: 'User1 teste',
            email: 'teste1@gmail.com',
            password: password
        });
        await User.create({
            name: 'User2 teste',
            email: 'teste2@gmail.com',
            password: password
        });
    
        console.log("Usuário criado:", firstUser.toJSON());

    } catch (error) {
       console.error(error.message) 
    }
}