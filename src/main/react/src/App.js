import React from 'react';
import $ from 'jquery';

import {Navbar, Nav, Form, Button, Image, Container, Row, Col, Accordion, Card, ListGroup, Toast, Modal, Alert, Badge} from 'react-bootstrap';
import Frame from "react-frame-component";

const url = (window.location.hostname === "localhost" ? "http://" : "https://")
    + window.location.hostname + ":" + window.location.port + "/";
const webSocket = new WebSocket((window.location.hostname === "localhost" ? "ws://" : "wss://")
    + window.location.hostname + ":" + window.location.port + "/tofu");

function isMember(username, users) {
  for (let i = 0; i < users.length; ++i) {
    if (users[i].username === username) {
      return true;
    }
  }
  return false;
}

class ChatNavBar extends React.Component {
  render() {
    return (
        <Navbar bg="white" expand="lg" style={{paddingRight: "30px"}}>
          <Navbar.Brand href="#home" style={{fontSize: '30px', fontWeight: "500"}}>
            <Image alt="logo" src="image/logo.png" width="80" height="80" className="d-inline-block align-center"/>
            Rice Chat
          </Navbar.Brand>
          <Nav className="ml-auto align-center" style={{fontSize: '20px'}}>
            <Nav.Item>User: {this.props.username}</Nav.Item>
          </Nav>
        </Navbar>
    )
  }
}

class ChatRoomMember extends React.Component {
  constructor(props) {
    super(props);
    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(event) {
    if (event.target.tagName === "BUTTON") {
      this.props.switchUser(event.target.lastChild.id);
    } else {
      this.props.switchUser(event.target.id);
    }
    this.props.clearCount(this.props.group, this.props.username);
  }

  showBadge(count) {
    if (count === undefined || count === 0) {
      return <></>
    } else {
      return <Badge variant={"secondary"} style={{marginLeft: "10px"}} id={this.props.group + '_' + this.props.username}>{count}</Badge>
    }
  }

  render() {
    return (
        <ListGroup.Item action variant={"none"} onClick={this.handleClick}>
          <Image id={this.props.group + '_' + this.props.username} src={"image/" + this.props.role + ".png"} style={{height: "20px"}} />
          <div className={"btn"} id={this.props.group + '_' + this.props.username}>{this.props.username}{this.showBadge(this.props.count)}</div>
        </ListGroup.Item>
    )
  }
}

class ChatRoomAction extends React.Component {
  constructor(props) {
    super(props);

    this.handleBroadcast = this.handleBroadcast.bind(this);
    this.handleLeave = this.handleLeave.bind(this);
    this.handleJoin = this.handleJoin.bind(this);
    this.handleClose = this.handleClose.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleJoinError = this.handleJoinError.bind(this);
    this.handleSend = this.handleSend.bind(this);
    this.handleChange = this.handleChange.bind(this);

    this.state = {
      message: "",
      broadcastError: "",
      showJoinError: false,
      showBroadcastPanel: false
    }
  }

  handleBroadcast() {
    this.setState({ showBroadcastPanel: true });
  }

  handleJoin() {
    $.post(url + "join", JSON.stringify({
      username: this.props.username,
      groupName: this.props.chatRoom.groupName
    }), this.handleJoinError);
  }

  handleLeave() {
    $.post(url + "leave", JSON.stringify({
      username: this.props.username,
      groupName: this.props.chatRoom.groupName
    }));
  }

  handleJoinError(text) {
    let data = JSON.parse(text);
    if (data.status === 403) {
      this.setState({showJoinError: true});
    }
  }

  handleClose(event) {
    this.setState({ showJoinError: false });
  }

  handleCancel(event) {
    this.setState({ showBroadcastPanel: false});
  }

  handleSend(event) {
    if (this.state.message === "") {
      this.setState({ broadcastError: "Broadcast message cannot be empty."});
    } else {
      this.setState({ broadcastError: "", showBroadcastPanel: false});
      this.props.sendMessage(JSON.stringify({
        type: "all",
        groupName: this.props.chatRoom.groupName,
        sender: this.props.username,
        receiver: "",
        text: this.state.message
      }));
    }
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  render() {
    if (this.props.chatRoom.owner === this.props.username) {
      return (
          <div className={"btn float-right"} style={{padding: "auto"}}>
            <Image src="image/broadcast.png" style={{height: "20px"}} onClick={this.handleBroadcast}/>
            <Modal show={this.state.showBroadcastPanel} onHide={this.handleCancel}>
              <Modal.Header closeButton>
                <Modal.Title>Broadcast</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <Form>
                  <Form.Group controlId="broadcastMessage">
                    <Form.Control name={"message"} value={this.state.message} as="textarea" rows="3" onChange={this.handleChange}/>
                    <Form.Text className="text-muted">
                      {this.state.broadcastError}
                    </Form.Text>
                  </Form.Group>
                </Form>
              </Modal.Body>
              <Modal.Footer>
                <Button variant="secondary" onClick={this.handleCancel}>
                  Cancel
                </Button>
                <Button variant="secondary" onClick={this.handleSend}>
                  Send
                </Button>
              </Modal.Footer>
            </Modal>
          </div>
      )
    } else if (isMember(this.props.username, this.props.chatRoom.joinedUsers)) {
      return (
          <div className={"btn float-right"} style={{padding: "auto"}}>
            <Image src="image/leave.png" style={{height: "20px"}} onClick={this.handleLeave}/>
          </div>
      )
    } else {
      return (
          <div className={"btn float-right"} style={{padding: "auto"}}>
            <Image src="image/join.png" style={{height: "20px"}} onClick={this.handleJoin}/>
            <Modal show={this.state.showJoinError} onHide={this.handleClose}>
              <Modal.Header closeButton>
                <Modal.Title>Sorry</Modal.Title>
              </Modal.Header>
              <Modal.Body>You are not allowed to join this chat room due to restrictions.</Modal.Body>
              <Modal.Footer>
                <Button variant="secondary" onClick={this.handleClose}>
                  Close
                </Button>
              </Modal.Footer>
            </Modal>
          </div>
      )
    }
  }
}

class ChatRoom extends React.Component {
  listChatRoomMembers() {
    return this.props.chatRoom.joinedUsers.filter(member => member.username !== this.props.username).map(member =>
        <ChatRoomMember key={this.props.chatRoom.groupName + '_' + member.username}
                        count={this.props.count === undefined ? undefined : this.props.count[member.username]}
                        switchUser={this.props.switchUser}
                        clearCount={this.props.clearCount}
                        group={this.props.chatRoom.groupName}
                        username={member.username}
                        role={member.username === this.props.chatRoom.owner ? "admin" : "user"}/>
    );
  }

  sumAll(count) {
    let sum = 0;
    let values = Object.values(count);
    for (let i=0; i<values.length; ++i) {
      sum += values[i];
    }
    return sum;
  }

  showBadge(count) {
    if (count === undefined || this.sumAll(count) === 0) {
      return <></>;
    } else {
      return <Badge variant={"secondary"} style={{marginLeft: "10px"}}>{this.sumAll(count)}</Badge>
    }
  }

  render() {
    return (
        <Card>
          <Accordion.Toggle as={Card.Header} eventKey={this.props.id}>
            <div className={"btn"} disabled={true} style={{fontWeight: "500"}}>{this.props.chatRoom.groupName}{this.showBadge(this.props.count)}</div>
            <ChatRoomAction chatRoom={this.props.chatRoom} username={this.props.username} sendMessage={this.props.sendMessage}/>
          </Accordion.Toggle>
          <Accordion.Collapse eventKey={this.props.id}>
            <Card.Body>
              <ListGroup variant={"flush"}>
                {this.listChatRoomMembers()}
              </ListGroup>
            </Card.Body>
          </Accordion.Collapse>
        </Card>
    )
  }
}

class ChatRoomList extends React.Component {
  constructor(props) {
    super(props);

    this.handleOpen = this.handleOpen.bind(this);
    this.handleClose = this.handleClose.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleCreate = this.handleCreate.bind(this);
    this.handleResponse = this.handleResponse.bind(this);

    this.state = {
      showCreatePanel: false,
      groupName: "",
      minAge: "",
      maxAge: "",
      region: ["na"],
      school: ["rice"],
      groupNameError: "",
      ageError: "",
      regionError: "",
      schoolError: "",
      createChatRoomError: ""
    };
  }

  handleChange(event) {
    if (event.target.name === "region" || event.target.name === "school") {
      let options = event.target.options;
      let values = [];
      for (let i=0; i<options.length; ++i ) {
        if (options[i].selected) {
          values.push(options[i].value);
        }
      }
      this.setState({
        [event.target.name]: values
      })
    } else {
      this.setState({
        [event.target.name]: event.target.value
      });
    }
  }

  handleCreate(event) {
    let regx = /^[0-9a-zA-Z]+$/;
    if (!regx.test(this.state.groupName)) {
      this.setState({groupNameError: "Only letters and numbers are allowed"});
      return;
    } else {
      this.setState({groupNameError: ""});
    }

    let minAge = parseInt(this.state.minAge);
    let maxAge = parseInt(this.state.maxAge);
    if (isNaN(minAge) || isNaN(maxAge) || minAge < 0 || maxAge < 0 ) {
      this.setState({ageError: "Age must be an integer no less than 0"});
      return;
    } else {
      this.setState({ageError: ""});
    }

    if (this.state.region.length === 0) {
      this.setState({regionError: "Must select at least 1 region"});
    } else {
      this.setState({regionError: ""});
    }

    if (this.state.school.length === 0) {
      this.setState({schoolError: "Must select at least 1 school"});
    } else {
      this.setState({schoolError: ""});
    }

    let data = {
      groupName: this.state.groupName,
      owner: this.props.username,
      minAge: this.state.minAge,
      maxAge: this.state.maxAge,
      locations: this.state.region,
      schools: this.state.school
    };
    $.post(url + "create", JSON.stringify(data), this.handleResponse)
  }

  handleResponse(text) {
    let data = JSON.parse(text);
    if (data.status === 200) {
      this.setState({showCreatePanel: false, createChatRoomError: ""});
    } else {
      this.setState({showCreatePanel: true, createChatRoomError: "Chat room name already used. Please choose another one."});
    }
  }

  showAlert(error) {
    if (error !== "") {
      return <Alert variant={"danger"}>{error}</Alert>
    }
  }

  handleOpen() {
    this.setState({showCreatePanel: true});
  }

  handleClose() {
    this.setState({showCreatePanel: false});
  }

  listRooms() {
    let idx = 0;
    let chatRooms = this.props.list.owner.concat(this.props.list.member, this.props.list.other);
    return chatRooms.map((chatRoom) =>
      <ChatRoom key={chatRoom.groupName}
                id={idx++}
                count={this.props.count[chatRoom.groupName]}
                chatRoom={chatRoom}
                username={this.props.username}
                switchUser={this.props.switchUser}
                clearCount={this.props.clearCount}
                sendMessage={this.props.sendMessage}/>
    )
  }

  render() {
    return (
        <>
          <Row>
            <Col>
              <Button variant="outline-secondary" size="lg" block style={{marginBottom: "20px"}} onClick={this.handleOpen}>
                Create Chat Room
              </Button>
              <Modal size="md" aria-labelledby="contained-modal-title-vcenter" centered show={this.state.showCreatePanel} onHide={this.handleClose}>
                <Modal.Header closeButton>
                  <Modal.Title id="contained-modal-title-vcenter">
                    Create Chat Room
                  </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                  <Form>
                    <Form.Group controlId="groupName">
                      <Form.Label>Chat Room Name</Form.Label>
                      <Form.Control name="groupName" type="text" placeholder="Choose your chat room name" value={this.state.groupName} onChange={this.handleChange}/>
                      <Form.Text className="text-muted">
                        {this.state.groupNameError}
                      </Form.Text>
                    </Form.Group>

                    <Form.Row>
                      <Form.Group as={Col} controlId="age">
                        <Form.Label>Min Age</Form.Label>
                        <Form.Control name="minAge" type="text" placeholder="Enter min age" value={this.state.minAge} onChange={this.handleChange}/>
                        <Form.Text className="text-muted">
                          {this.state.ageError}
                        </Form.Text>
                      </Form.Group>
                      <Form.Group as={Col} controlId="age">
                        <Form.Label>Max Age</Form.Label>
                        <Form.Control name="maxAge" type="text" placeholder="Enter max age" value={this.state.maxAge} onChange={this.handleChange}/>
                      </Form.Group>
                    </Form.Row>

                    <Form.Group controlId="region">
                      <Form.Label>Region</Form.Label>
                      <Form.Control name="region" as="select" value={this.state.region} onChange={this.handleChange} multiple>
                        <option value="na">NA</option>
                        <option value="as">AS</option>
                        <option value="eu">EU</option>
                        <option vlaue="sa">SA</option>
                        <option value="oa">OA</option>
                        <option value="af">AF</option>
                      </Form.Control>
                      <Form.Text className="text-muted">
                        {this.state.regionError}
                      </Form.Text>
                    </Form.Group>

                    <Form.Group controlId="school">
                      <Form.Label>School</Form.Label>
                      <Form.Control name="school" as="select" value={this.state.school} onChange={this.handleChange} multiple>
                        <option value="rice">Rice</option>
                        <option value="tamu">TAMU</option>
                        <option value="ut-austin">UT-Austin</option>
                        <option value="ut-dallas">UT-Dallas</option>
                      </Form.Control>
                      <Form.Text className="text-muted">
                        {this.state.schoolError}
                      </Form.Text>
                    </Form.Group>

                    {this.showAlert(this.state.createChatRoomError)}

                  </Form>
                </Modal.Body>
                <Modal.Footer>
                  <Button variant={"secondary"} onClick={this.handleCreate}>Create</Button>
                </Modal.Footer>
              </Modal>
            </Col>
          </Row>
          <Row>
            <Col>
              <Accordion style={{marginBottom: "20px"}}>
                {this.listRooms()}
              </Accordion>
            </Col>
          </Row>
        </>
    )
  }
}

class ChatDialog extends React.Component {
  listDialogs() {
    let idx = 0;
    return this.props.chat.map((dialog) => {
      if (dialog.user === this.props.username) {
        return (
            <Row key={idx++}>
              <Col>
                <div className={"float-right"}>
                  <Card style={{marginBottom: "20px"}}>
                    <Card.Body style={{paddingTop: "10px", paddingBottom: "10px"}}>{dialog.text}</Card.Body>
                  </Card>
                </div>
              </Col>
            </Row>
        )
      } else {
        return (
            <Row key={idx++}>
              <Col>
                <div className={"float-left"}>
                  <Card style={{marginBottom: "20px"}}>
                    <Card.Body style={{paddingTop: "10px", paddingBottom: "10px"}}>{dialog.text}</Card.Body>
                  </Card>
                </div>
              </Col>
            </Row>
        )
      }
    })
  }

  render() {
    return (
        <Container style={{margin: "0", width: "auto", maxWidth: "none"}}>
          {this.listDialogs()}
        </Container>
    );
  }
}

class ChatPanel extends React.Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.handleSend = this.handleSend.bind(this);

    this.state = {
      message: "",
      messageError: ""
    }
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  handleSend() {
    if (this.state.message === "") {
      this.setState({ messageError: "Message cannot be empty"} );
    } else {
      this.setState({ message: "", messageError: "" });
      let data = {
        type: "one",
        groupName: this.props.group,
        sender: this.props.username,
        receiver: this.props.user,
        text: this.state.message
      };
      this.props.sendMessage(JSON.stringify(data));
      this.props.addMessage(data);
    }
  }

  render() {
    if (this.props.user === "") {
      return <></>
    }
    return (
        <Card style={{height: "80vh"}}>
          <Card.Header>{"Room: " + this.props.group + " / User: " + this.props.user}</Card.Header>
          <Card.Body>
            <Frame width={"100%"} height={"100%"} frameBorder={"no"} style={{overflow: "hidden"}} initialContent={
              "<!DOCTYPE html><html><head>" +
              "<link rel=\"stylesheet\"\n" +
              "href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\"\n" +
              "integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\"\n" +
              "crossorigin=\"anonymous\"\n" +
              "/>" +
              "<style>body::-webkit-scrollbar{display:none}</style>\n" +
              "</head><body><div></div></body></html>"}>
              <ChatDialog chat={this.props.chat} username={this.props.username}/>
            </Frame>
          </Card.Body>
          <Card.Footer className="text-right">
            <Form>
              <Form.Group>
                <Form.Control as={"textarea"} rows={"3"} name={"message"} value={this.state.message} onChange={this.handleChange}/>
                <Form.Text className="text-muted">
                  {this.state.messageError}
                </Form.Text>
              </Form.Group>
              <Button variant={"secondary"} onClick={this.handleSend}>Send</Button>
            </Form>
          </Card.Footer>
        </Card>
    )
  }
}

class Notification extends React.Component {
  showHeader(msg) {
    if (msg.type === "all") {
      return "Group " + msg.sender + " Broadcast";
    } else if (msg.type === "system") {
      return "System Notification";
    }
  }

  render() {
    let size = this.props.note.length;
    if (size === 0) {
      return <></>;
    }
    return (
        <div style={{position: "absolute", right: "30px", top: "30px"}}>
          <Toast autohide delay={5000} onClose={this.props.handleClose} show={this.props.show}>
            <Toast.Header>
              <strong className="mr-auto">{this.showHeader(this.props.note[size-1])}</strong>
            </Toast.Header>
            <Toast.Body>{this.props.note[size-1].text}</Toast.Body>
          </Toast>
        </div>
    );
  }
}

class Register extends React.Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.handleRegister = this.handleRegister.bind(this);
    this.state = {
      username: "",
      age: "",
      region: "na",
      school: "rice",
      usernameError: "",
      ageError: ""
    };
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  handleRegister(event) {
    let regx = /^[0-9a-zA-Z]+$/;
    if (!regx.test(this.state.username)) {
      this.setState({usernameError: "Only letters and numbers are allowed"});
      return;
    } else {
      this.setState({usernameError: ""});
    }
    let age = parseInt(this.state.age);
    if (isNaN(age) || age <= 0) {
      this.setState({ageError: "Age must be an integer greater than 0"});
      return;
    } else {
      this.setState({ageError: ""});
    }

    let data = {
      type: "register",
      groupName: "",
      sender: this.state.username,
      receiver: "",
      text: JSON.stringify({
        username: this.state.username,
        age: this.state.age,
        location: this.state.region,
        school: this.state.school
      })
    };
    this.props.sendMessage(JSON.stringify(data));
  }

  showAlert(error) {
    if (error !== "") {
      return <Alert variant={"danger"}>{error}</Alert>
    }
  }

  render() {
    return (
        <Modal size="md" aria-labelledby="contained-modal-title-vcenter" centered show={!this.props.registered}>
          <Modal.Header>
            <Modal.Title id="contained-modal-title-vcenter">
              Register
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form>
              <Form.Group controlId="username">
                <Form.Label>Username</Form.Label>
                <Form.Control name="username" type="text" placeholder="Choose your username" value={this.state.username} onChange={this.handleChange}/>
                <Form.Text className="text-muted">
                  {this.state.usernameError}
                </Form.Text>
              </Form.Group>

              <Form.Group controlId="age">
                <Form.Label>Age</Form.Label>
                <Form.Control name="age" type="text" placeholder="Enter your age" value={this.state.age} onChange={this.handleChange}/>
                <Form.Text className="text-muted">
                  {this.state.ageError}
                </Form.Text>
              </Form.Group>

              <Form.Group controlId="region">
                <Form.Label>Region</Form.Label>
                <Form.Control name="region" as="select" value={this.state.region} onChange={this.handleChange}>
                  <option value="na">NA</option>
                  <option value="as">AS</option>
                  <option value="eu">EU</option>
                  <option vlaue="sa">SA</option>
                  <option value="oa">OA</option>
                  <option value="af">AF</option>
                </Form.Control>
              </Form.Group>

              <Form.Group controlId="school">
                <Form.Label>School</Form.Label>
                <Form.Control name="school" as="select" value={this.state.school} onChange={this.handleChange}>
                  <option value="rice">Rice</option>
                  <option value="tamu">TAMU</option>
                  <option value="ut-austin">UT-Austin</option>
                  <option value="ut-dallas">UT-Dallas</option>
                </Form.Control>
              </Form.Group>

              {this.showAlert(this.props.registerError)}

            </Form>
          </Modal.Body>
          <Modal.Footer>
            <Button variant={"secondary"} onClick={this.handleRegister}>Register</Button>
          </Modal.Footer>
        </Modal>
    );
  }
}

class AccessError extends React.Component {
  render() {
    return (
        <Modal show={this.props.show} onHide={this.props.handleClose}>
          <Modal.Header closeButton>
            <Modal.Title>Sorry</Modal.Title>
          </Modal.Header>
          <Modal.Body>You have to join the chat room before chatting with members</Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={this.props.handleClose}>
              Close
            </Button>
          </Modal.Footer>
        </Modal>
    )
  }
}

class App extends React.Component {
  constructor(props) {
    super(props);
    webSocket.onmessage = (msg) => this.onMessage(msg);

    this.switchUser = this.switchUser.bind(this);
    this.updateList = this.updateList.bind(this);
    this.getList = this.getList.bind(this);
    this.sendMessage = this.sendMessage.bind(this);
    this.addMessage = this.addMessage.bind(this);
    this.clearCount = this.clearCount.bind(this);
    this.handleNoteClose = this.handleNoteClose.bind(this);
    this.handleAccessErrClose = this.handleAccessErrClose.bind(this);

    this.state = {
      list: {
        owner: [],
        member: [],
        other: []
      },
      chat: {_: []},
      note: [],
      count: {},
      group: "",
      user: "",
      registered: false,
      registerError: "",
      accessError: false,
      username: "",
      notification: false
    };
  }

  updateList(data) {
    data = JSON.parse(data);
    this.setState({list: data});
    let groups = data.owner.concat(data.member, data.other).filter(group => group.groupName === this.state.group);
    if (groups.length === 0
        || groups[0].joinedUsers.filter(user => user.username === this.state.user).length === 0
        || groups[0].joinedUsers.filter(user => user.username === this.state.username).length === 0 ) {
      this.setState({ group: "", user: ""});
    }
  }

  getList() {
    $.post(url + "list", JSON.stringify({username: this.state.username}), this.updateList);
  }

  onMessage(msgEvent) {
    let msg = JSON.parse(msgEvent.data);
    if (msg.type === "ping") {
      console.log("keep connection alive");
    } else if (msg.type === "register") {
      if (msg.text === "success") {
        this.setState({
          username: msg.receiver,
          registered: true,
        });
        this.getList();
      } else if (msg.text === "conflict") {
        this.setState({
          registerError: "Username conflict. Please use another one."
        })
      }
    } else if (msg.type === "one") {
      this.addMessage(msg);
      let state = this.state.count;
      if (this.state.user !== msg.sender) {
        if (state[msg.groupName] === undefined) {
          state[msg.groupName] = {[msg.sender]: 1};
        } else if (state[msg.groupName][msg.sender] === undefined) {
          state[msg.groupName][msg.sender] = 1;
        } else {
          state[msg.groupName][msg.sender] += 1;
        }
      }
      this.setState({ count: state});
    } else {
      this.addNotification(msg);
    }
  }

  addMessage(msg) {
    let chat = this.state.chat;
    if (msg.sender === this.state.username && chat[msg.groupName + "_" + msg.receiver] === undefined) {
      chat[msg.groupName + "_" + msg.receiver] = [{user: msg.sender, text: msg.text}];
    } else if (msg.receiver === this.state.username && chat[msg.groupName + "_" + msg.sender] === undefined) {
      chat[msg.groupName + "_" + msg.sender] = [{user: msg.sender, text: msg.text}];
    } else if (msg.sender === this.state.username) {
      chat[msg.groupName + "_" + msg.receiver].push({user: msg.sender, text: msg.text});
    } else if (msg.receiver === this.state.username) {
      chat[msg.groupName + "_" + msg.sender].push({user: msg.sender, text: msg.text});
    }
    this.setState({chat: chat});
  }

  addNotification(msg) {
    let note = this.state.note;
    note.push(msg);
    this.setState({note: note, notification: true});
    this.getList();
  }

  switchUser(id) {
    let fields = id.split('_');
    let rooms = this.state.list.other;
    for (let i=0; i<rooms.length; ++i) {
      if (rooms[i].groupName === fields[0]) {
        this.setState({ accessError: true });
        return;
      }
    }

    let chat = this.state.chat;
    if (chat[id] === undefined) {
      chat[id] = [];
    }
    this.setState({group: fields[0], user: fields[1], chat: chat});
  }

  sendMessage(msg) {
    if (msg !== "") {
      webSocket.send(msg);
    }
    msg = JSON.parse(msg);
    if (msg.type === "one") {
      this.addNotification({
        type: "system",
        groupName: msg.groupName,
        sender: msg.sender,
        receiver: msg.receiver,
        text: "Message delivered"
      })
    }
  }

  clearCount(groupName, username) {
    let count = this.state.count;
    if (count[groupName] !== undefined && count[groupName][username] !== undefined) {
      count[groupName][username] = 0;
    }
    this.setState({ count: count});
  }

  handleNoteClose() {
    this.setState({ notification: false });
  }

  handleAccessErrClose() {
    this.setState({ accessError: false });
  }

  render() {
    return (
        <div>
          <ChatNavBar username={this.state.username}/>
          <Container style={{margin: "0", width: "auto", maxWidth: "none"}}>
            <Row style={{margin: "0"}}>
              <Col md={3}>
                <ChatRoomList list={this.state.list}
                              count={this.state.count}
                              username={this.state.username}
                              switchUser={this.switchUser}
                              clearCount={this.clearCount}
                              sendMessage={this.sendMessage}/>
              </Col>
              <Col md={9}>
                <ChatPanel chat={this.state.chat[this.state.group + '_' + this.state.user]}
                           user={this.state.user}
                           group={this.state.group}
                           username={this.state.username}
                           sendMessage={this.sendMessage}
                           addMessage={this.addMessage}/>
              </Col>
            </Row>
          </Container>
          <Notification note={this.state.note} show={this.state.notification} handleClose={this.handleNoteClose}/>
          <Register registered={this.state.registered} registerError={this.state.registerError} sendMessage={this.sendMessage} />
          <AccessError show={this.state.accessError} handleClose={this.handleAccessErrClose}/>
        </div>
    );
  }
}

export default App;
