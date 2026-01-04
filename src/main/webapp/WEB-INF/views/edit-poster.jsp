<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Create New Post - Programmize</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
  <!-- Quill Editor CSS -->
  <link href="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">

  <style>
    :root {
      --primary-color: #2F7AF7;
      --secondary-color: #FF6B6B;
      --accent-color: #4ECDC4;
      --dark-bg: #1a1a2e;
      --light-bg: #f8f9fa;
      --border-color: #e0e0e0;
    }

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background-color: var(--light-bg);
      padding-top: 80px; /* Add space for fixed header */
    }

    /* Header */
    .page-header {
      background: linear-gradient(135deg, var(--primary-color) 0%, #7b68ee 100%);
      color: white;
      padding: 30px 0;
      margin-bottom: 40px;
      margin-top: -12px;
    }

    .page-header h1 {
      font-size: 2.5rem;
      font-weight: 700;
      margin-bottom: 10px;
    }

    .page-header p {
      font-size: 1.1rem;
      opacity: 0.9;
      margin: 0;
    }

    /* Main Content */
    .create-post-container {
      max-width: 1200px;
      margin: 0 auto;
      padding-bottom: 60px;
    }

    .form-card {
      background: white;
      border-radius: 20px;
      padding: 40px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.08);
      margin-bottom: 30px;
    }

    .form-section-title {
      font-size: 1.3rem;
      font-weight: 700;
      color: #333;
      margin-bottom: 25px;
      padding-bottom: 15px;
      border-bottom: 2px solid var(--light-bg);
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .form-section-title i {
      color: var(--primary-color);
    }

    .form-label {
      font-weight: 600;
      color: #333;
      margin-bottom: 10px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .form-label .required {
      color: var(--secondary-color);
      font-size: 0.9rem;
    }

    .form-control, .form-select {
      border: 2px solid var(--border-color);
      border-radius: 10px;
      padding: 12px 16px;
      font-size: 0.95rem;
      transition: all 0.3s;
    }

    .form-control:focus, .form-select:focus {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 0.2rem rgba(47, 122, 247, 0.1);
    }

    textarea.form-control {
      resize: vertical;
      min-height: 120px;
    }

    /* File Upload Area */
    .upload-area {
      border: 3px dashed var(--border-color);
      border-radius: 15px;
      padding: 40px;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;
      background: var(--light-bg);
    }

    .upload-area:hover {
      border-color: var(--primary-color);
      background: rgba(47, 122, 247, 0.05);
    }

    .upload-area.dragover {
      border-color: var(--primary-color);
      background: rgba(47, 122, 247, 0.1);
    }

    .upload-icon {
      font-size: 3rem;
      color: var(--primary-color);
      margin-bottom: 15px;
    }

    .upload-text {
      color: #666;
      font-size: 1rem;
      margin-bottom: 10px;
    }

    .upload-subtext {
      color: #999;
      font-size: 0.85rem;
    }

    #fileInput {
      display: none;
    }

    .preview-container {
      margin-top: 20px;
      display: none;
    }

    .preview-image {
      max-width: 100%;
      max-height: 400px;
      border-radius: 15px;
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }

    .remove-image {
      margin-top: 15px;
      color: var(--secondary-color);
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 5px;
      font-size: 0.9rem;
      font-weight: 600;
    }

    .remove-image:hover {
      text-decoration: underline;
    }

    /* Quill Editor */
    #editor-container {
      height: 400px;
      border-radius: 10px;
    }

    .ql-toolbar {
      border-radius: 10px 10px 0 0;
      border: 2px solid var(--border-color);
      border-bottom: 1px solid var(--border-color);
    }

    .ql-container {
      border-radius: 0 0 10px 10px;
      border: 2px solid var(--border-color);
      border-top: 0;
      font-size: 1rem;
    }

    a {
      text-decoration: none;
    }

    /* Status Selection */
    .status-options {
      display: flex;
      gap: 15px;
    }

    .status-option {
      flex: 1;
      position: relative;
    }

    .status-option input[type="radio"] {
      display: none;
    }

    .status-label {
      display: block;
      padding: 20px;
      border: 2px solid var(--border-color);
      border-radius: 15px;
      cursor: pointer;
      transition: all 0.3s;
      text-align: center;
    }

    .status-label:hover {
      border-color: var(--primary-color);
      transform: translateY(-2px);
    }

    .status-option input[type="radio"]:checked + .status-label {
      border-color: var(--primary-color);
      background: rgba(47, 122, 247, 0.1);
    }

    .status-icon {
      font-size: 2rem;
      margin-bottom: 10px;
    }

    .status-title {
      font-weight: 600;
      font-size: 1.1rem;
      margin-bottom: 5px;
    }

    .status-description {
      font-size: 0.85rem;
      color: #666;
    }

    /* Action Buttons */
    .action-buttons {
      display: flex;
      gap: 15px;
      justify-content: flex-end;
      margin-top: 30px;
    }

    .btn-custom {
      padding: 14px 35px;
      border-radius: 12px;
      font-weight: 600;
      font-size: 1rem;
      transition: all 0.3s;
      border: none;
      display: inline-flex;
      align-items: center;
      gap: 10px;
    }

    .btn-primary-custom {
      background: linear-gradient(135deg, var(--primary-color), #7b68ee);
      color: white;
    }

    .btn-primary-custom:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(47, 122, 247, 0.3);
    }

    .btn-secondary-custom {
      background: white;
      color: #666;
      border: 2px solid var(--border-color);
    }

    .btn-secondary-custom:hover {
      border-color: var(--primary-color);
      color: var(--primary-color);
    }

    /* Helper Text */
    .form-text {
      color: #999;
      font-size: 0.85rem;
      margin-top: 8px;
    }

    /* Character Counter */
    .char-counter {
      text-align: right;
      font-size: 0.85rem;
      color: #999;
      margin-top: 5px;
    }

    .char-counter.warning {
      color: var(--secondary-color);
    }

    /* Responsive */
    @media (max-width: 768px) {
      .form-card {
        padding: 25px;
      }

      .page-header h1 {
        font-size: 2rem;
      }

      .status-options {
        flex-direction: column;
      }

      .action-buttons {
        flex-direction: column;
      }

      .btn-custom {
        width: 100%;
        justify-content: center;
      }
    }
  </style>
</head>
<body>

<jsp:include page="include/header.jsp" />

<!-- Page Header -->
<div class="page-header">
  <div class="container">
    <h1><i class="fas fa-pen-to-square"></i> Edit Poster</h1>
    <p>Modify your poster's information</p>
  </div>
</div>

<c:if test="${not empty sessionScope.errorMessage}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    <i class="fas fa-exclamation-triangle"></i> ${sessionScope.errorMessage}
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
  <c:remove var="errorMessage" scope="session"/>
</c:if>

<c:if test="${not empty poster}">
  <div class="container create-post-container">
    <form id="createPostForm" action="/blog/edit-poster/${poster.slug}" method="post" enctype="multipart/form-data">

      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

      <div class="form-card">
        <h3 class="form-section-title">
          <i class="fas fa-file-alt"></i>
          Basic Information
        </h3>

        <!-- Title -->
        <div class="mb-4">
          <label for="title" class="form-label">
            Post Title
            <span class="required">*</span>
          </label>
          <input type="text" class="form-control" id="title" name="title"
                 placeholder="Enter an engaging title for your post..."
                 required maxlength="200" value="${poster.title}">
          <div class="char-counter" id="titleCounter">0 / 200</div>
        </div>

        <!-- Excerpt -->
        <div class="mb-4">
          <label for="excerpt" class="form-label">
            Excerpt
            <span class="required">*</span>
          </label>
          <textarea class="form-control" id="excerpt" name="excerpt"
                    placeholder="Brief summary of your post (will be shown in blog listing)..."
                    required maxlength="500">${poster.excerpt}</textarea>
          <div class="char-counter" id="excerptCounter">0 / 500</div>
          <div class="form-text">
            <i class="fas fa-info-circle"></i> This will appear as a preview in the blog list
          </div>
        </div>

        <!-- Category -->
        <div class="mb-4">
          <label for="category" class="form-label">
            Category
            <span class="required">*</span>
          </label>
          <select class="form-select" id="category" name="categoryId" required>
            <option value="" disabled>Select a category...</option>
            <c:forEach items="${allCategories}" var="cat">
              <option value="${cat.id}"
                      <c:if test="${cat.id == poster.category.id}">selected</c:if>>
                  ${cat.name}
              </option>
            </c:forEach>
          </select>
          <div class="form-text">
            <i class="fas fa-info-circle"></i> Choose the most relevant category for your post
          </div>
        </div>
      </div>

      <!-- Thumbnail Upload -->
      <div class="form-card">
        <h3 class="form-section-title">
          <i class="fas fa-image"></i>
          Thumbnail Image
        </h3>

        <!-- Upload area -->
        <div class="upload-area" id="uploadArea"
             style="<c:if test='${not empty poster.thumbnailUrl}'>display:none;</c:if>">
          <div class="upload-icon">
            <i class="fas fa-cloud-upload-alt"></i>
          </div>
          <div class="upload-text">
            <strong>Click to upload</strong> or drag and drop
          </div>
          <div class="upload-subtext">
            PNG, JPG, WEBP up to 5MB (Recommended: 1200x600px)
          </div>
        </div>

        <input type="file" id="fileInput" name="thumbnail"
               accept="image/png,image/jpeg,image/jpg,image/webp">

        <!-- Preview -->
        <div class="preview-container" id="previewContainer"
             style="<c:if test='${not empty poster.thumbnailUrl}'>display:block;</c:if>">
          <img src="${poster.thumbnailUrl}"
               alt="Preview"
               class="preview-image"
               id="previewImage">

          <div>
            <input type="hidden" name="removeThumbnail" id="removeThumbnail" value="false">
      <span class="remove-image" id="removeImage">
        <i class="fas fa-trash"></i> Remove Image
      </span>
          </div>
        </div>
      </div>

      <!-- Content Editor -->
      <div class="form-card">
        <h3 class="form-section-title">
          <i class="fas fa-edit"></i>
          Post Content
        </h3>

        <label class="form-label">
          Content
          <span class="required">*</span>
        </label>
        <div id="editor-container"></div>
        <input type="hidden" id="content" name="content" required>
        <div class="form-text mt-3">
          <i class="fas fa-info-circle"></i> Use the editor toolbar to format your content with headings, lists, images, and code blocks
        </div>
      </div>

      <!-- Status Selection -->
      <div class="form-card">
        <h3 class="form-section-title">
          <i class="fas fa-toggle-on"></i>
          Publication Status
        </h3>

        <div class="status-options">
          <div class="status-option">
            <input type="radio" id="statusDraft" name="status" value="0" checked>
            <label for="statusDraft" class="status-label">
              <div class="status-icon">
                <i class="fas fa-file-pen" style="color: #FFA500;"></i>
              </div>
              <div class="status-title">Draft</div>
              <div class="status-description">Save as draft, not visible to others</div>
            </label>
          </div>

          <div class="status-option">
            <input type="radio" id="statusPublish" name="status" value="1">
            <label for="statusPublish" class="status-label">
              <div class="status-icon">
                <i class="fas fa-globe" style="color: #4CAF50;"></i>
              </div>
              <div class="status-title">Publish</div>
              <div class="status-description">Make post public immediately</div>
            </label>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="action-buttons">
        <a href="javascript:history.back()" class="btn-custom btn-secondary-custom">
          <i class="fas fa-times"></i>
          Cancel
        </a>
        <button type="submit" class="btn-custom btn-primary-custom">
          <i class="fas fa-check"></i>
          Save Changes
        </button>
      </div>
    </form>
  </div>
</c:if>

<jsp:include page="include/footer.jsp" />

<!-- Quill Editor JS -->
<script src="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
  // Initialize Quill Editor
  const quill = new Quill('#editor-container', {
    theme: 'snow',
    placeholder: 'Write your post content here...',
    modules: {
      toolbar: [
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'align': [] }],
        ['link', 'image', 'code-block'],
        ['clean']
      ]
    }
  });

  const existingContent = `<c:out value="${poster.content}" escapeXml="false"/>`;
  if (existingContent && existingContent.trim().length > 0) {
    quill.root.innerHTML = existingContent;
  }

  // Character counters
  const titleInput = document.getElementById('title');
  const excerptInput = document.getElementById('excerpt');
  const titleCounter = document.getElementById('titleCounter');
  const excerptCounter = document.getElementById('excerptCounter');

  titleInput.addEventListener('input', function() {
    const count = this.value.length;
    titleCounter.textContent = `${count} / 200`;
    if (count > 180) {
      titleCounter.classList.add('warning');
    } else {
      titleCounter.classList.remove('warning');
    }
  });

  excerptInput.addEventListener('input', function() {
    const count = this.value.length;
    excerptCounter.textContent = `${count} / 500`;
    if (count > 450) {
      excerptCounter.classList.add('warning');
    } else {
      excerptCounter.classList.remove('warning');
    }
  });

  // File upload functionality
  const uploadArea = document.getElementById('uploadArea');
  const fileInput = document.getElementById('fileInput');
  const previewContainer = document.getElementById('previewContainer');
  const previewImage = document.getElementById('previewImage');
  const removeImage = document.getElementById('removeImage');

  uploadArea.addEventListener('click', () => {
    fileInput.click();
  });

  // Drag and drop
  uploadArea.addEventListener('dragover', (e) => {
    e.preventDefault();
    uploadArea.classList.add('dragover');
  });

  uploadArea.addEventListener('dragleave', () => {
    uploadArea.classList.remove('dragover');
  });

  uploadArea.addEventListener('drop', (e) => {
    e.preventDefault();
    uploadArea.classList.remove('dragover');
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      handleFile(files[0]);
    }
  });

  fileInput.addEventListener('change', (e) => {
    if (e.target.files.length > 0) {
      handleFile(e.target.files[0]);
    }
  });

  function handleFile(file) {
    // Validate file type
    if (!file.type.match('image.*')) {
      alert('Please upload an image file');
      return;
    }

    // Validate file size (5MB)
    if (file.size > 5 * 1024 * 1024) {
      alert('File size must be less than 5MB');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      previewImage.src = e.target.result;
      uploadArea.style.display = 'none';
      previewContainer.style.display = 'block';
    };
    reader.readAsDataURL(file);
  }

  removeImage.addEventListener('click', () => {
    fileInput.value = '';
    previewImage.src = '';
    previewContainer.style.display = 'none';
    uploadArea.style.display = 'block';
    document.getElementById('removeThumbnail').value = 'true';
  });

  // Form submission
  document.getElementById('createPostForm').addEventListener('submit', function(e) {
    // Get content from Quill editor
    const content = quill.root.innerHTML;
    document.getElementById('content').value = content;

    // Validate content is not empty
    if (quill.getText().trim().length === 0) {
      e.preventDefault();
      alert('Please enter post content');
      return false;
    }

    const hasExistingThumbnail = document.getElementById('previewImage').src !== '';
    if (!fileInput.files.length && !hasExistingThumbnail) {
      e.preventDefault();
      alert('Please upload a thumbnail image');
      return false;
    }
  });
</script>
</body>
</html>