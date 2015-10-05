package mini

import Chisel._

case class BrCondIn(brType: BigInt, rs1: BigInt, rs2: BigInt)
case class BrCondOut(taken: Boolean)

object GoldBrCond {
  import Control._
  def toBigInt(x: Int) = (BigInt(x >>> 1) << 1) | (x & 0x1)
  def apply(in: BrCondIn) = new BrCondOut(if (in.brType == BR_EQ.litValue()) in.rs1 == in.rs2
    else if (in.brType == BR_NE.litValue()) in.rs1 != in.rs2
    else if (in.brType == BR_LT.litValue()) in.rs1.toInt < in.rs2.toInt
    else if (in.brType == BR_GE.litValue()) in.rs1.toInt >= in.rs2.toInt
    else if (in.brType == BR_LTU.litValue()) in.rs1 < in.rs2
    else if (in.brType == BR_GEU.litValue()) in.rs1 >= in.rs2 else false)
}

class BrCondTests[+T <: BrCond](c: T) extends Tester(c) with RISCVCommon {
  override val insts = (List.fill(10){List(
    B(Funct3.BEQ, 0, 0, 0),
    B(Funct3.BNE, 0, 0, 0),
    B(Funct3.BLT, 0, 0, 0),
    B(Funct3.BGE, 0, 0, 0),
    B(Funct3.BLTU, 0, 0, 0),
    B(Funct3.BGEU, 0, 0, 0))}).flatten

  for (inst <- insts) {
    val a = int(rnd.nextInt)
    val b = int(rnd.nextInt)
    val ctrl = GoldControl(inst)
    val gold = GoldBrCond(new BrCondIn(ctrl(5), a, b))
    println("*** %s -> A: %x, B: %x***".format(dasm(inst), a, b))
    poke(c.io.br_type, ctrl(5))
    poke(c.io.rs1, a)
    poke(c.io.rs2, b)
    expect(c.io.taken, gold.taken) 
  }
}
