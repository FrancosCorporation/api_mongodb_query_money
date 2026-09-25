import mongoose from 'mongoose';

// Port de Models_client_create.java: id (UUID), name, password (BCrypt), email, datas
const schema = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  name: { type: String, required: true },
  password: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  dataCreate: { type: Date, default: Date.now },
  changeDate: { type: Date, default: Date.now }
}, { versionKey: false });

export const Client = mongoose.model('Client', schema);

// Models_data_list_names.java / Models_data_b3_names.java: id, nameAction
const schemaAcao = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  nameAction: { type: String, required: true }
}, { versionKey: false });
export const ActionName = mongoose.model('ActionName', schemaAcao);

// Models_layout_data_client.java: id (do cliente) + lista de acoes da carteira
const itemAcao = new mongoose.Schema({
  nameAction: String,
  dateBuy: String,
  priceBuy: String,
  quantity: String
}, { _id: false });

const schemaCarteira = new mongoose.Schema({
  id: { type: String, required: true, unique: true },
  actionsAndPrice: { type: [itemAcao], default: [] }
}, { versionKey: false });
export const Wallet = mongoose.model('Wallet', schemaCarteira);

// Models_data_b3.java: cotacao historica (B3)
const schemaB3 = new mongoose.Schema({
  Date: String, Open: String, High: String, Low: String,
  Close: String, Price: String, Volume: String
}, { versionKey: false });
export const DataB3 = mongoose.model('DataB3', schemaB3);
