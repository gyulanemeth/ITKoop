require 'em-websocket'
require 'uuid'
require 'json'

uuid = UUID.new
puts uuid

EventMachine.run {
  @channel = EM::Channel.new

EventMachine::WebSocket.start(:host => "0.0.0.0", :port => 8080, :debug => false) do |ws|
  # timer = nil
  #     timer = EM.add_periodic_timer(20) {
  #     p ["Sent time",
  #       ws.send(JSON.generate({
  #         msg: Time.now.strftime("%T")
  #       }))]
  #   }
  ws.onopen {

    sid = @channel.subscribe { |m| ws.send JSON.generate({ msg: m, sender: "server" })}

    @channel.push JSON.generate({ msg: "#{sid} connected!", sender: "server" })

    puts "#{sid} connected!"

    ws.onmessage { |value|
      m = JSON.parse(value)["msg"]
      puts m

      @channel.push JSON.generate({ msg: m, sender: sid })

      puts "Sending"
    }

    ws.onclose {
      @channel.push JSON.generate({ msg: "#{sid} left!", sender: "server" })
      @channel.unsubscribe(sid)
      puts "#{sid} left"
    }

    ws.onerror { |e|
      puts "Error: #{e.message}"
    }
  }
  end
}