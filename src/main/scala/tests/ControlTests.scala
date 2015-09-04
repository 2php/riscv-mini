package mini

import Chisel._
import TestCommon._

class ControlTests(c: Control) extends Tester(c) {
  def checkCtrl(ctrl: Array[BigInt]) {
    expect(c.io.ctrl.pc_sel,  ctrl(0))
    expect(c.io.ctrl.A_sel,   ctrl(1))
    expect(c.io.ctrl.B_sel,   ctrl(2))
    expect(c.io.ctrl.imm_sel, ctrl(3))
    expect(c.io.ctrl.alu_op,  ctrl(4))
    expect(c.io.ctrl.br_type, ctrl(5))
    expect(c.io.ctrl.st_type, ctrl(7))
    expect(c.io.ctrl.csr_cmd, ctrl(11))
    expect(c.io.ctrl.xpt,     ctrl(12))
    step(1)
    expect(c.io.ctrl.ld_type, ctrl(8))
    expect(c.io.ctrl.wb_sel,  ctrl(9))
    expect(c.io.ctrl.wb_en,   ctrl(10))
  }

  poke(c.io.ctrl.stall, 0)
  for ((inst, i) <- insts.zipWithIndex) {
    println("***** %s *****".format(dasm(inst)))
    poke(c.io.ctrl.inst, inst.litValue())
    checkCtrl(decode(inst))
  }
}
