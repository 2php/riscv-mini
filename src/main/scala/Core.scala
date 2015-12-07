package mini

import Chisel._
import cde.{Parameters, Field}

case object XLEN extends Field[Int]
case object BuildALU extends Field[Parameters => ALU]
case object BuildImmGen extends Field[Parameters => ImmGen]
case object BuildBrCond extends Field[Parameters => BrCond]

abstract trait CoreParams {
  implicit val p: Parameters
  val xlen = p(XLEN)
}

abstract class CoreBundle(implicit val p: Parameters) extends junctions.ParameterizedBundle()(p) with CoreParams

class HostIO(implicit p: Parameters) extends CoreBundle()(p) {
  val fromhost = Valid(UInt(width=xlen)).flip
  val tohost   = UInt(OUTPUT, xlen)
}

class CoreIO(implicit p: Parameters) extends Bundle {
  val host = new HostIO
  val icache = (new CacheIO).flip
  val dcache = (new CacheIO).flip
}

class Core(implicit p: Parameters) extends Module {
  val io = new CoreIO
  val dpath = Module(new Datapath) 
  val ctrl  = Module(new Control)

  io.host <> dpath.io.host
  io.icache <> dpath.io.icache
  io.dcache <> dpath.io.dcache
  dpath.io.ctrl <> ctrl.io.ctrl
}
