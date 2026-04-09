import { Sequelize } from "sequelize";

const sequelize = new Sequelize("auth-db-spring-2026","admin","123456",
    {
        host: "localhost",
        port: 5434,
        dialect: "postgres",
        quoteIdentifiers:false,
        define: {
            syncOnAssociation: true,
            timestamps:false,
            underscored:true,
            underscoredAll:true,
            freezeTableName:true,
        },
        pool: {
            acquire: 180000,
        },
    });
sequelize
.authenticate()
.then(()=>{
    console.log('conection has been stablished')
} )
.catch(err => {
    console.error('unable to conect')
    console.error("essa é a mensagem de erro "+err.message)
});
export default sequelize;