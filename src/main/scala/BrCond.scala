package mini

import Chisel._
import cde.Parameters
import Control._

class BrCondIO(implicit p: Parameters) extends CoreBundle()(p) {
  val rs1 = UInt(INPUT, xlen)
  val rs2 = UInt(INPUT, xlen)
  val br_type = UInt(INPUT, 3)
  val taken = Bool(OUTPUT)
}

abstract class BrCond(implicit val p: Parameters) extends Module {
  val io = new BrCondIO
}

class BrCondSimple(implicit p: Parameters) extends BrCond()(p) {
  val eq   = io.rs1 === io.rs2
  val neq  = !eq
  val lt   = io.rs1.toSInt < io.rs2.toSInt
  val ge   = !lt
  val ltu  = io.rs1 < io.rs2
  val geu  = !ltu
  io.taken :=     
    ((io.br_type === BR_EQ) && eq) ||
    ((io.br_type === BR_NE) && neq) ||
    ((io.br_type === BR_LT) && lt) ||
    ((io.br_type === BR_GE) && ge) ||
    ((io.br_type === BR_LTU) && ltu) ||
    ((io.br_type === BR_GEU) && geu)
}

class BrCondArea(implicit p: Parameters) extends BrCond()(p) with CoreParams {
  val diff = io.rs1 - io.rs2
  val neq  = diff.orR
  val eq   = !neq
  val isSameSign = io.rs1(xlen-1) === io.rs2(xlen-1)
  val lt   = Mux(isSameSign, diff(xlen-1), io.rs1(xlen-1))
  val ltu  = Mux(isSameSign, diff(xlen-1), io.rs2(xlen-1))
  val ge   = !lt
  val geu  = !ltu
  io.taken :=     
    ((io.br_type === BR_EQ) && eq) ||
    ((io.br_type === BR_NE) && neq) ||
    ((io.br_type === BR_LT) && lt) ||
    ((io.br_type === BR_GE) && ge) ||
    ((io.br_type === BR_LTU) && ltu) ||
    ((io.br_type === BR_GEU) && geu)
}
