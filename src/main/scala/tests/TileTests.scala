package mini

import Chisel._
import Chisel.AdvTester._
import junctions.{MemReqCmd, MemData, MemResp}
import scala.collection.mutable.{Queue => ScalaQueue}

case class TestMemReq(addr: Int, tag: BigInt, rw: Boolean)
case class TestMemData(data: BigInt)
case class TestMemResp(data: BigInt, tag: BigInt)

class TileMem(cmdQ: ScalaQueue[TestMemReq],
            dataQ: ScalaQueue[TestMemData],
            respQ: ScalaQueue[TestMemResp],
            word_width: Int = 16, depth: Int = 1 << 20) extends SimMem(word_width, depth) {
  def process {
    if (!cmdQ.isEmpty && !dataQ.isEmpty && cmdQ.front.rw) {
      val cmd = cmdQ.dequeue
      val data = dataQ.dequeue
      write(cmd.addr, data.data)
    } else if (!cmdQ.isEmpty && !cmdQ.front.rw) {
      val cmd = cmdQ.dequeue
      respQ enqueue new TestMemResp(read(cmd.addr), cmd.tag)
    } 
  }
}

class TileTester(c: Tile, args: Array[String]) extends AdvTester(c, false) with MemTests {
  val cmdHandler = new DecoupledSink(c.io.mem.req_cmd, 
    (cmd: MemReqCmd) => new TestMemReq(peek(cmd.addr).toInt, peek(cmd.tag), peek(cmd.rw) != 0))
  val dataHandler = new DecoupledSink(c.io.mem.req_data, 
    (data: MemData) => new TestMemData(peek(data.data)))
  val respHandler = new DecoupledSource(c.io.mem.resp,
    (resp: MemResp, in: TestMemResp) => {reg_poke(resp.data, in.data) ; reg_poke(resp.tag, in.tag)})
  val mem = new TileMem(cmdHandler.outputs, dataHandler.outputs, respHandler.inputs, 16)
  preprocessors += mem
  cmdHandler.process()
  dataHandler.process()
  respHandler.process()
  def regFile(x: Int) = peekAt(c.core.dpath.regFile.regs, x)
  def loadMem(testname: String) = mem.loadMem(testname)
  def loadMem(test: Seq[UInt]) = mem.loadMem(test)
  def runTests(maxcycles: Int, verbose: Boolean) {
    cycles = 0
    ok &= run(c.io.htif.host, maxcycles, verbose)
  }
  val (file, tests, maxcycles, verbose) = parseOpts(args)
  start(file, tests, maxcycles, verbose)
}
