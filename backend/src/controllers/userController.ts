import { Request, Response } from "express";
import User from "../models/User";

export const createUser = async (req: Request, res: Response) => {
  try {
    const user = new User(req.body); // expects { name: "Alice" }
    await user.save();
    res.status(201).json(user);
  } catch (err) {
    res.status(400).json({ error: "User creation failed" });
  }
};

export const listUsers = async (_req: Request, res: Response) => {
  const users = await User.find().lean();
  res.json(users);
};
