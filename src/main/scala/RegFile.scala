package mini

import chisel3._
import cde.Parameters

class RegFileIO(implicit p: Parameters)  extends CoreBundle()(p) {
  val raddr1 = Input(UInt(5))
  val raddr2 = Input(UInt(5))
  val rdata1 = Output(UInt(xlen))
  val rdata2 = Output(UInt(xlen))
  val wen    = Input(Bool())
  val waddr  = Input(UInt(5))
  val wdata  = Input(UInt(xlen))
}

class RegFile(implicit val p: Parameters) extends Module with CoreParams {
  val io = IO(new RegFileIO)
  val regs = Mem(32, UInt(width=xlen))
  io.rdata1 := Mux(io.raddr1.orR, regs(io.raddr1), UInt(0))
  io.rdata2 := Mux(io.raddr2.orR, regs(io.raddr2), UInt(0))
  when(io.wen & io.waddr.orR) {
    regs(io.waddr) := io.wdata
  }
}
